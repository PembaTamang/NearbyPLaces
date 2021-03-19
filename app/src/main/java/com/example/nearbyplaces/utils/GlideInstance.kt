package com.example.nearbyplaces.utils

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.nearbyplaces.R

class GlideInstance {
    companion object{
        fun glideInstance(application: Application): RequestManager {
            val options = RequestOptions().placeholder(R.drawable.ph)
            return Glide.with(application).applyDefaultRequestOptions(options)
        }
    }
}
