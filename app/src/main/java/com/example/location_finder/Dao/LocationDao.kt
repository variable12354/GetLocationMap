package com.example.location_finder.Dao

import androidx.room.*
import com.example.location_finder.Model.Locations

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locations: Locations)

    @Query("SELECT * FROM Locations")
    suspend fun select():List<Locations>

    @Query("DELETE FROM Locations WHERE id = :recId")
    suspend fun deleteDocument(recId: Int)

    @Update
    suspend fun updateDocument(locations: Locations)

    @Query("UPDATE Locations SET name = :name, address = :address, Latitude = :lati, Longitude = :longi WHERE id = :id")
    suspend fun updateLocation(name:String,address:String,lati:Double,longi:Double,id:Int)

    @Query("UPDATE Locations SET distance = :distance WHERE id = :id")
    suspend fun updateDistance(distance:Float,id:Int)


}