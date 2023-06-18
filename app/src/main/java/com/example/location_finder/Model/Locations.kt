package com.example.location_finder.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "Locations")
data class Locations(

    @ColumnInfo(name = "Name")
    var name:String,

    @ColumnInfo(name = "Address")
    var address:String,

    @ColumnInfo(name = "Latitude")
    var lati:Double,

    @ColumnInfo(name = "Longitude")
    var longitude:Double,

    @ColumnInfo(name = "Distance")
    var distance:Float

):Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null

    constructor() : this("", "", 0.0, 0.0,0f)
}
