package com.example.nearbyplaces.utils

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.nearbyplaces.R

class GlideInstance {
    companion object{
        fun getInstance(context: Context): RequestManager {
            val options = RequestOptions().placeholder(R.drawable.ph)
            return Glide.with(context).applyDefaultRequestOptions(options)
        }
    }
}
