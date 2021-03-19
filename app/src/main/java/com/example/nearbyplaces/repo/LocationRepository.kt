package com.example.nearbyplaces.repo


import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.nearbyplaces.consts.Consts
import com.example.nearbyplaces.models.LocationResponse
import com.example.nearbyplaces.network.ApiInstance
import com.example.nearbyplaces.utils.hasInternet
import com.example.nearbyplaces.utils.mlog
import com.example.nearbyplaces.utils.noInternet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.Request
import java.io.IOException

import java.lang.Exception

class LocationRepository(application: Application) {

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create()
            .apply {
                interval = 5000
                fastestInterval = 4000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }

   private var locationLiveData: MutableLiveData<LocationResponse> = MutableLiveData()
    val context = application.applicationContext
    private var fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    private lateinit var noInternetImpl: noInternet
    fun setAlertlistener(noInternet: noInternet) {
        noInternetImpl = noInternet
    }

    fun getLiveDatafromRepo(): MutableLiveData<LocationResponse> {
        return locationLiveData
    }

    fun callApiFromRepo(location: String, keyword: String) {
        if (hasInternet(context)){
            try {
                val clientInstance = ApiInstance.getInstance()
                val request = Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$location&keyword=$keyword&rankby=distance&key=${Consts.API_KEY}")
                    .build()
                clientInstance.newCall(request).enqueue(object :  Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        mlog( e.message.toString())
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val locationResponse:LocationResponse = Gson().fromJson(response.body!!.string(),LocationResponse::class.java)
                        mlog("success ${locationResponse.results.size}")
                        locationLiveData.postValue(locationResponse)
                    }

                })

            } catch (exception: Exception) {
                noInternetImpl.error()
                mlog("failure")
            }
        }else{
            noInternetImpl.noInternetAlert()
        }

    }

}