package com.example.nearbyplaces.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LocationViewModelFactory (private var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)){
            return LocationViewModel(application) as T
        }
        throw IllegalArgumentException("Error while creating view model")
    }
}