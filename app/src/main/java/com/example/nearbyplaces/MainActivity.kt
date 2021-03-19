package com.example.nearbyplaces

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nearbyplaces.consts.Consts
import com.example.nearbyplaces.databinding.ActivityMainBinding
import com.example.nearbyplaces.utils.mlog
import com.example.nearbyplaces.utils.noInternet
import com.example.nearbyplaces.viewmodel.LocationViewModel
import com.example.nearbyplaces.viewmodel.LocationViewModelFactory


class MainActivity : AppCompatActivity(), noInternet {
    var permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var locationViewModel: LocationViewModel
    lateinit var locationViewModelFactory: LocationViewModelFactory
    private lateinit var manager: LocationManager
    private var currentLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationViewModelFactory = LocationViewModelFactory(application)
        locationViewModel =
            ViewModelProvider(this, locationViewModelFactory).get(LocationViewModel::class.java)
        locationViewModel.setListener(this)
        locationViewModel.callApi("27.0601825,88.2770396", "gyms|food")

        locationViewModel.getLiveData().observe(this) {
            mlog(it.results.size.toString())
        }
        manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!hasPermissions(this, *permissions)) {
            mlog("requesting permission")
            requestPermissions(this, permissions, Consts.REQUEST_CODE_LOCATION_PERMISSION)
        } else {
            mlog("has permissions")
            getCurrentLocation()
        }
        binding.filter.setOnClickListener {
            
        }
    }

    private fun getCurrentLocation() {
        if (!statusCheck()) {
            buildAlertMessageNoGps()
        }
        locationViewModel.getLocationData().observe(this) {
            currentLocation = it
            mlog("newlocation $it")
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
}