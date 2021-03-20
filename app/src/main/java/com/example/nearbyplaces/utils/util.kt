package com.example.nearbyplaces.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.nearbyplaces.BuildConfig
import com.example.nearbyplaces.consts.Consts
import com.example.nearbyplaces.consts.Consts.Companion.TAG


fun hasInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            //It will check for both wifi and cellular network
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
        return false
    } else {
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}


interface noInternet {
    fun noInternetAlert()
    fun error()
}

fun mlog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(TAG, message)
    }
}
//https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=
//AIzaSyC1BIF3FT3LKxcGLg6Uf1bkHWIpgdbtf2A

fun getPhotoUrl(reference:String?):String{
    if(reference.isNullOrBlank()){
        mlog("no image")
        return "https://via.placeholder.com/500x500?text=Image+Not+Found"
    }
return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$reference&key=${Consts.API_KEY}"
}
