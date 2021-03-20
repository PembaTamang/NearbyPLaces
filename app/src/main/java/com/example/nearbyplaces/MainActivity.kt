package com.example.nearbyplaces

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyplaces.SPHelper.set
import com.example.nearbyplaces.adapter.RecyclerAdapter
import com.example.nearbyplaces.consts.Consts
import com.example.nearbyplaces.consts.Consts.Companion.MyPreference
import com.example.nearbyplaces.databinding.ActivityMainBinding
import com.example.nearbyplaces.databinding.FilterAlertBinding
import com.example.nearbyplaces.models.ModelFilter
import com.example.nearbyplaces.models.ModelRecycler
import com.example.nearbyplaces.utils.getPhotoUrl
import com.example.nearbyplaces.utils.mlog
import com.example.nearbyplaces.utils.noInternet
import com.example.nearbyplaces.viewmodel.LocationViewModel
import com.example.nearbyplaces.viewmodel.LocationViewModelFactory
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.StringBuilder


class MainActivity : AppCompatActivity(), noInternet, CompoundButton.OnCheckedChangeListener {
    var permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var types = ArrayList<ModelFilter>()
    private var arraylistRecycler = ArrayList<ModelRecycler>()
    private lateinit var recycler : RecyclerView
    private lateinit var adapter : RecyclerAdapter
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var _alertBinding: FilterAlertBinding? = null
    private val alertBinding get() = _alertBinding!!

    private lateinit var sphelper : SharedPreferences
    lateinit var locationViewModel: LocationViewModel
    lateinit var locationViewModelFactory: LocationViewModelFactory
    private lateinit var manager: LocationManager
    private var currentLocation: Location? = null
    private var loadinglistData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = getSystemService(LOCATION_SERVICE) as LocationManager

        //setting recyclerview
        recycler = binding.recycler
        adapter = RecyclerAdapter(arraylistRecycler){
            Toast.makeText(this, arraylistRecycler[it].name, Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter

        //setting up viewmodel
        locationViewModelFactory = LocationViewModelFactory(application)
        locationViewModel = ViewModelProvider(this, locationViewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setListener(this)
        locationViewModel.getLiveData().observe(this) {
            binding.progress.visibility = GONE
            arraylistRecycler.clear()
         it.results.forEach { results ->
             arraylistRecycler.add(ModelRecycler(results.name,getPhotoUrl(results?.photos?.get(0)?.photo_reference),results.types[0]))
         }.also {
             adapter.notifyDataSetChanged()
             arraylistRecycler.forEach { data->
                 mlog(data.imgUrl)
             }
         }
        }
        //checking permissions
        if (!hasPermissions(this, *permissions)) {
            mlog("requesting permission")
            requestPermissions(this, permissions, Consts.REQUEST_CODE_LOCATION_PERMISSION)
        } else {
            mlog("has permissions")
            getCurrentLocation()
        }


        binding.filter.setOnClickListener {
            if(currentLocation == null){
                Toast.makeText(this, "Current location has not been found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setUpFilterAlert()
        }


         sphelper = SPHelper.mPrefs(this)
        types.addAll(
            arrayListOf(
                ModelFilter("gyms", 100),
                ModelFilter("food", 200),
                ModelFilter("restaurant", 300),
                ModelFilter("cafe", 400)
            )
        )

    }



    private fun getCurrentLocation() {
        if (!statusCheck()) {
            buildAlertMessageNoGps()
        }
        binding.progress.visibility = VISIBLE
           locationViewModel.getLocationData().observe(this) {
               currentLocation = it
               mlog("current location updated")
               if(binding.progress.isVisible && !loadinglistData){
                   binding.progress.visibility = GONE
               }
           }
    }


    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun statusCheck(): Boolean = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun noInternetAlert() {
        runOnUiThread {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }

    }

    override fun error() {
        runOnUiThread {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
        }

    }


    private fun setUpFilterAlert() {
        _alertBinding = FilterAlertBinding.inflate(LayoutInflater.from(this))
        val alertDialogBuilder = MaterialAlertDialogBuilder(this)
        val selectAll = alertBinding.selectAll
        val clearAll = alertBinding.clearAll
        alertDialogBuilder.setView(alertBinding.root)
        val layoutcontainer = alertBinding.choicesContainer

        types.forEach {
            val checkBox = MaterialCheckBox(this).apply {
                text = it.name
                id = it.id
                isChecked = sphelper.getBoolean(it.id.toString(),false)
                setOnCheckedChangeListener(this@MainActivity)
            }
            layoutcontainer.addView(checkBox)
        }

        selectAll.isChecked = sphelper.getBoolean("selectAll",false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        selectAll.setOnCheckedChangeListener { _, isChecked ->
            types.forEach {
                sphelper[it.id.toString()] = isChecked
                val checkbox = layoutcontainer.findViewById<MaterialCheckBox>(it.id)
                checkbox.isChecked = isChecked
            }
            sphelper["selectAll"] = isChecked
        }


        clearAll.setOnClickListener {itemID ->
            getSharedPreferences(MyPreference, MODE_PRIVATE).edit().clear().apply()
            sphelper[itemID.id.toString()] = false
            selectAll.isChecked = false
            types.forEach {
                val checkbox = layoutcontainer.findViewById<MaterialCheckBox>(it.id)
                checkbox.isChecked = false
            }
        }

        alertBinding.submit.setOnClickListener {

            //send query
            val keywords = StringBuilder()
            types.forEach {
                if(sphelper.getBoolean(it.id.toString(),false)){
                    keywords.append(it.name).append("|")
                }
            }
            if(keywords.toString().isBlank()){
                Toast.makeText(this, "Please make your choice", Toast.LENGTH_SHORT).show()
            }
            loadinglistData = true
            binding.progress.visibility = VISIBLE
            mlog("keywords ${keywords.toString()}")
            locationViewModel.callApi(location = "${currentLocation!!.latitude},${currentLocation!!.longitude}",keyword = keywords.toString())
            alertDialog.dismiss()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        sphelper[buttonView!!.id.toString()] = isChecked
    }
}