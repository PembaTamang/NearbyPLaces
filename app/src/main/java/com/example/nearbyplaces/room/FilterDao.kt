package com.example.nearbyplaces.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ArrayList<ModelEntity>)

    @Query("select * from modelentity")
    fun getFilterData():LiveData<List<ModelEntity>>
}