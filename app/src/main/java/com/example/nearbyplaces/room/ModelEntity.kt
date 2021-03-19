package com.example.nearbyplaces.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class ModelEntity(val name: String, val checked: Boolean){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}

