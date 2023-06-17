package com.example.location_finder.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.location_finder.Dao.LocationDao
import com.example.location_finder.Model.Locations

@Database(entities = [Locations::class], version = 1, exportSchema = false)
abstract class LocationDatabase :RoomDatabase() {

    abstract fun locationDao(): LocationDao
}