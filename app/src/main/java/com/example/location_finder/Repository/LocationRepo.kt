package com.example.location_finder.Repository


import com.example.location_finder.Dao.LocationDao
import com.example.location_finder.Model.Locations
import javax.inject.Inject

class LocationRepo @Inject constructor(private val locationDao: LocationDao) {

    suspend fun insert(locations: Locations) = locationDao.insert(locations)

    suspend fun select() = locationDao.select()

    suspend fun delete(recid:Int) = locationDao.deleteDocument(recid)

    suspend fun update(locations: Locations) = locationDao.updateDocument(locations)

    suspend fun updateLocation(name:String,address:String,lati:Double,longi:Double,id:Int) = locationDao.updateLocation(name, address, lati, longi, id)

    suspend fun updateDistance(distance:Float,id: Int) = locationDao.updateDistance(distance,id)
}
