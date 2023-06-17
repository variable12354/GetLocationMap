package com.example.location_finder

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.location_finder.LocationViewModel.LocationViewModel
import com.example.location_finder.Model.Locations
import com.example.location_finder.databinding.ActivityGoogleMapBinding
import com.example.location_finder.databinding.AddlocationSheetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.widget.PlaceSelectionError
import com.google.android.libraries.places.ktx.widget.PlaceSelectionSuccess
import com.google.android.libraries.places.ktx.widget.placeSelectionEvents
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat


@AndroidEntryPoint
class GoogleMap : AppCompatActivity(), OnMapReadyCallback {

    private val locationViewModel:LocationViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityGoogleMapBinding
    private var lati:String? = null
    private var long:String? = null
    var dialog: BottomSheetDialog? = null
    var eid:Int? = 0
    var ename:String? = ""
    var eaddress:String? = ""
    var elati:Double? = 0.0
    var elong:Double? = 0.0
    var from:String? = ""
    private var mapFragment:Fragment? = null
    private lateinit var addBottom:AddlocationSheetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        eid = intent.getIntExtra("id",0) ?: 0
        ename = intent.getStringExtra("name") ?: ""
        eaddress = intent.getStringExtra("address") ?: ""
        elati = intent.getDoubleExtra("lati", 0.0)
        elong = intent.getDoubleExtra("long", 0.0)
        from = intent.getStringExtra("From")
        Log.e("TAG", "Froom:$from ")
        Log.e("TAG", "onCreateEdit:$eid ")
        Log.e("TAG", "onCreateEdit:$ename ")
        Log.e("TAG", "onCreateEdit:$eaddress ")
        Log.e("TAG", "onCreateEdit:$elati ")
        Log.e("TAG", "onCreateEdit:$elong ")
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        (mapFragment as SupportMapFragment).getMapAsync(this)

        locationViewModel.select()
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_map_apikey))
        }

        placesClient = Places.createClient(this)

        val autocompleteSupportFragment1 = supportFragmentManager.findFragmentById(R.id.place_autocomplete) as AutocompleteSupportFragment?
        autocompleteSupportFragment1?.setPlaceFields(
            listOf(
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS
            )
        )



        // Listen to place selection events
        lifecycleScope.launchWhenCreated {
            autocompleteSupportFragment1?.placeSelectionEvents()?.collect { event ->
                when (event) {
                    is PlaceSelectionSuccess -> {
                        val place = event.place
                        lati = place.latLng?.latitude.toString()
                        long = place.latLng?.longitude.toString()
                        Log.e("TAG", "Name:${place.name} ")
                        Log.e("TAG", "Address:${place.address} ")
                        Log.e("TAG", "Lati:${place.latLng.latitude} ")
                        Log.e("TAG", "Long:${place.latLng.longitude} ")
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(place.latLng).title(place.name))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))


                        dialog = BottomSheetDialog(this@GoogleMap,R.style.BottomSheetDialog)
                        addBottom = AddlocationSheetBinding.inflate(layoutInflater)
                        dialog!!.setContentView(addBottom.root)

                        if (!eid.toString().isNullOrEmpty() && eid!! > 0) {

                            addBottom.addLocation.isVisible = false
                            addBottom.editLocation.isVisible = true
                            addBottom.tvTitle.text = getString(R.string.update)
                        }else{
                            addBottom.addLocation.isVisible = true
                            addBottom.editLocation.isVisible = false
                            addBottom.tvTitle.text = getString(R.string.add)
                        }


                        addBottom.addLocation.setOnClickListener {
                            /*locationViewModel.select()
                            locationViewModel.locationList.observe(this@GoogleMap) {

                                if (it.size > 1) {
                                    it.forEach {it2->
                                        var distance =  distance2(it.first().lati,it.first().longitude,it2.lati,it2.longitude)
                                        Log.e("TAG", "onCreate:Distance $distance ")

                                        val locData = Locations(name = place.name, address = place.address , lati = place.latLng.latitude, longitude = place.latLng.longitude, distance = distance )
                                        locationViewModel.insert(locData)
                                    }
                                }
                                else{
                                    val locData = Locations(name = place.name, address = place.address , lati = place.latLng.latitude, longitude = place.latLng.longitude, distance = 0f )
                                    locationViewModel.insert(locData)
                                }

                            }*/
                            val locData = Locations(name = place.name, address = place.address , lati = place.latLng.latitude, longitude = place.latLng.longitude,distance = 0f)
                            locationViewModel.insert(locData)
                            startActivity(Intent(this@GoogleMap,MainActivity::class.java))
                            finish()
                            dialog?.dismiss()
                        }

                        addBottom.editLocation.setOnClickListener {

                            Log.e("TAG", "onCreateButton:$eid ")
                            Log.e("TAG", "onCreateButton:$ename ")
                            Log.e("TAG", "onCreateButton:$eaddress ")
                            Log.e("TAG", "onCreateButton:$elati ")
                            Log.e("TAG", "onCreateButton:$elong ")

                            locationViewModel.updateLocation(place.name,place.address, place.latLng.latitude, place.latLng.longitude, eid!!)
                            startActivity(Intent(this@GoogleMap,MainActivity::class.java))
                            finish()
                            dialog?.dismiss()
                        }
                        dialog?.setCanceledOnTouchOutside(false)
                        dialog?.setCancelable(false)
                        dialog?.show()


                    }
                    is PlaceSelectionError -> Toast.makeText(
                        this@GoogleMap,
                        "Failed to get place '${event.status.statusMessage}'",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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

        Log.e("TAG", "distance-${loc1.distanceTo(loc2)} ")
        Log.e("TAG", "dis-${format(mtr)} ")
        return radius * angle
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
        return mtr
    }

    fun format(distanceInMetres: Float): String? {
        val kms = distanceInMetres / 1000
        val formatter: NumberFormat = NumberFormat.getInstance()
        return formatter.format(kms)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (!eid.toString().isNullOrEmpty() && eid!! > 0)
        {
            val editloca = LatLng(elati!!, elong!!)
            mMap.addMarker(MarkerOptions().position(editloca).title(ename))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(editloca))
        }else{
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(MarkerOptions().position(sydney).title("place.name"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        /*val locations = listOf(
            LatLng(37.7749, -122.4194),  // San Francisco
            LatLng(34.0522, -118.2437),  // Los Angeles
            LatLng(47.6097, -122.3331)   // Seattle
        )*/


        if (from.equals("Direction"))
        {
           /* locationViewModel.locationList.observe(this@GoogleMap){
                val locationsList = ArrayList<LatLng>()
                it.forEach { list->
                    val latlongs = LatLng(list.lati,list.longitude)
                    locationsList.add(latlongs)
                    Log.e("TAG", "onMapReady:$latlongs ", )
                }
                drawRoute(locationsList)
            }*/

            val locations = listOf(
            LatLng(37.7749, -122.4194),  // San Francisco
            LatLng(34.0522, -118.2437),  // Los Angeles
            LatLng(47.6097, -122.3331)   // Seattle
        )
            drawRoute(locations)

        }


    }

    private fun drawRoute(locations: List<LatLng>) {
        for (i in 0 until locations.size - 1) {
            val origin = locations[i]
            val destination = locations[i + 1]

            mMap.addMarker(MarkerOptions().position(origin))
            mMap.addMarker(MarkerOptions().position(destination))

            val directionsResult = getDirections(origin, destination)
            if (directionsResult != null) {
                val points = decodePolyline(directionsResult.routes[0].overviewPolyline.encodedPath)
                val polylineOptions = PolylineOptions()
                polylineOptions.color(Color.RED)
                polylineOptions.width(5f)
                polylineOptions.addAll(points)
                mMap.addPolyline(polylineOptions)
            }
        }

        // Move camera to the first location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locations.first()))
    }

    private fun getDirections(origin: LatLng, destination: LatLng): DirectionsResult? {
        val geoApiContext = GeoApiContext.Builder()
            .apiKey("AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U")
            .build()

        return DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(origin.latitude.toString() + "," + origin.longitude.toString())
            .destination(destination.latitude.toString() + "," + destination.longitude.toString())
            .await()
    }

    private fun decodePolyline(encodedPath: String): List<LatLng> {
        val polyPoints = PolyUtil.decode(encodedPath)
        val points = mutableListOf<LatLng>()
        for (polyPoint in polyPoints) {
            points.add(LatLng(polyPoint.latitude, polyPoint.longitude))
        }
        return points
    }

    override fun onResume() {
        super.onResume()
        mapFragment?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment?.onLowMemory()
    }
}