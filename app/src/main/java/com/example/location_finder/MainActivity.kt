package com.example.location_finder

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.location_finder.Adapter.LocationAdapter
import com.example.location_finder.LocationViewModel.LocationViewModel
import com.example.location_finder.Model.Locations
import com.example.location_finder.databinding.ActivityMainBinding
import com.example.location_finder.databinding.SortingSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listadapter:LocationAdapter
    private val locationViewModel: LocationViewModel by viewModels()
    var dialog: BottomSheetDialog? = null
    private lateinit var sortsheet: SortingSheetBinding
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initView()
        locationViewModel.select()

        binding.sorting.setOnClickListener {
            dialog = BottomSheetDialog(this@MainActivity,R.style.BottomSheetDialog)
            sortsheet = SortingSheetBinding.inflate(layoutInflater)
            dialog!!.setContentView(sortsheet.root)
            sortsheet.ascText.setOnClickListener {
                Toast.makeText(this@MainActivity, "New First", Toast.LENGTH_SHORT).show()
                locationViewModel.locationList.observe(this) {
                    listadapter.setData(it.sortedBy {s-> s.distance})
                }
                dialog?.dismiss()
            }
            sortsheet.decText.setOnClickListener {
                locationViewModel.locationList.observe(this) {
                    listadapter.setData(it.sortedByDescending {s-> s.distance})
                }
                Toast.makeText(this@MainActivity, "New Seond", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }

            dialog?.setCanceledOnTouchOutside(true)
            dialog?.setCancelable(true)
            dialog?.show()
        }


        binding.addLocation.setOnClickListener {
            startActivity(Intent(this@MainActivity,GoogleMap::class.java))
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity,GoogleMap::class.java)
            intent.putExtra("From","Direction")
            startActivity(intent)
        }

        locationViewModel.locationList.observe(this){
            listadapter.setData(it)
            if (it.size > 1) {
                it.forEach {it2->
                    var distance =  distance2(it.first().lati,it.first().longitude,it2.lati,it2.longitude)
                    Log.e("TAG", "onCreate:Distance $distance ")
                    locationViewModel.updateDistance(distance, it2.id!!)
                }
            }

            Log.e("TAG", "new:${it.forEach { it2 -> it2.distance }} ")
            /*val latlong = LatLng(it.first().lati,it.first().longitude)
            Log.e("TAG", "sorting:$latlong ")
            it.forEach {it2->
                val distance =  distance(it.first().lati,it.first().longitude,it2.lati,it2.longitude)
                Log.e("TAG", "onCreate:Distance $distance ")
            }*/


        }



        listadapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
                Log.e("TAG", "onItemRangeRemoved: ")
            }
        })


        listadapter?.setListener {it->
            Log.e("TAG", "On create:$it ")
            locationViewModel.delete(it)

            locationViewModel.select()
            locationViewModel.locationList.observe(this@MainActivity, Observer {
                listadapter.setData(it)
            })
            listadapter.notifyDataSetChanged()
        }

        listadapter.onEdit {id,name,address,lati,long->

            val intent = Intent(this@MainActivity,GoogleMap::class.java)
            intent.putExtra("id",id)
            intent.putExtra("name",name)
            intent.putExtra("address",address)
            intent.putExtra("lati",lati)
            intent.putExtra("long",long)
            startActivity(intent)


        }


    }
    fun checkEmpty() {
        binding.animationView?.visibility = (if (listadapter==null || listadapter?.itemCount == 0) View.VISIBLE else View.GONE)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        listadapter = LocationAdapter(this@MainActivity)
        binding.rvLocation.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listadapter
        }
        listadapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        locationViewModel.locationList.observe(this@MainActivity){
            listadapter.setData(it)
        }
    }
    fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Double {
        val radius = 6378137.0 // approximate Earth radius, *in meters*
        val deltaLat = toLat - fromLat
        val deltaLon = toLon - fromLon
        val angle = 2 * Math.asin(
            Math.sqrt(
                Math.pow(
                    Math.sin(deltaLat / 2),
                    2.0
                ) + Math.cos(fromLat) * Math.cos(toLat) * Math.pow(
                    Math.sin(deltaLon / 2), 2.0
                )
            )
        )

        val loc1 = Location("")
        loc1.latitude = fromLat // current latitude

        loc1.longitude = fromLon //current  Longitude


        val loc2 = Location("")
        loc2.latitude = toLat
        loc2.longitude = toLon

        val mtr = loc1.distanceTo(loc2)

        Log.e("TAG", "distance:KM-${loc1.distanceTo(loc2)} ")
        Log.e("TAG", "KM-${format(mtr)} ")
        return radius * angle
    }

    fun format(distanceInMetres: Float): String? {
        val kms = distanceInMetres / 1000
        val formatter: NumberFormat = NumberFormat.getInstance()
        return formatter.format(kms)
    }

    fun distance2(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Float {

        val loc1 = Location("")
        loc1.latitude = fromLat // current latitude

        loc1.longitude = fromLon //current  Longitude

        val loc2 = Location("")
        loc2.latitude = toLat
        loc2.longitude = toLon

        val mtr = loc1.distanceTo(loc2)

        Log.e("TAG", "distance-${loc1.distanceTo(loc2)} ")
        Log.e("TAG", "dis-${format(mtr)} ")
        return (mtr/10000) // km
    }
}