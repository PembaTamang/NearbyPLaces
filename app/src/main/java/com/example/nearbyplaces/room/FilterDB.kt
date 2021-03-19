package com.example.nearbyplaces.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ModelEntity::class], version = 1, exportSchema = false)
abstract class FilterDB : RoomDatabase() {
    abstract fun getNoteDao(): FilterDao

    companion object {
        @Volatile
        private var instance: FilterDB? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, FilterDB::class.java, "filter.db")
                .build()

    }
}