package com.example.nearbyplaces.viewmodel


import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nearbyplaces.models.LocationResponse
import com.example.nearbyplaces.repo.LocationRepository
import com.example.nearbyplaces.utils.noInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : ViewModel() {

    private val repository = LocationRepository(application)
    private val locationData = LocationLiveData(application)

    fun getLocationData () : LiveData<Location> = locationData

    fun getLiveData(): MutableLiveData<LocationResponse> {
        return repository.getLiveDatafromRepo()
    }

    fun setListener(noInternet: noInternet) {
        repository.setAlertlistener(noInternet)
    }

    fun callApi(location: String, keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.callApiFromRepo(location, keyword)
        }
    }
}