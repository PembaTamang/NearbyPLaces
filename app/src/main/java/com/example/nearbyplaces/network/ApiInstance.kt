package com.example.nearbyplaces.network

import okhttp3.OkHttpClient

class ApiInstance {
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=27.0601825,88.2770396&keyword=gyms|food&rankby=distance&key=AIzaSyC1BIF3FT3LKxcGLg6Uf1bkHWIpgdbtf2A
    companion object{
        fun getInstance(): OkHttpClient {
            return  OkHttpClient()
        }
    }
}