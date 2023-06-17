package com.example.location_finder.LocationViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.location_finder.Model.Locations
import com.example.location_finder.Repository.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepo: LocationRepo) : ViewModel(){


    private val _locationList = MutableLiveData<List<Locations>>()
    val locationList:LiveData<List<Locations>> = _locationList

    fun insert(locations: Locations) = viewModelScope.launch {
        locationRepo.insert(locations)
    }

    fun select() = viewModelScope.launch {
        locationRepo.select().let {
            _locationList.postValue(it)
        }
    }

    fun delete(recid:Int) = viewModelScope.launch {
        locationRepo.delete(recid)
    }

    fun update(locations: Locations) = viewModelScope.launch {
        locationRepo.update(locations)
    }

    fun updateLocation(name:String,address:String,lati:Double,longi:Double,id:Int) = viewModelScope.launch {
        locationRepo.updateLocation(name, address, lati, longi, id)
    }

    fun updateDistance(distance:Float,id:Int) = viewModelScope.launch {
        locationRepo.updateDistance(distance,id)
    }




}