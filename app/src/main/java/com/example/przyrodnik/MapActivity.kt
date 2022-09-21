package com.example.przyrodnik

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var lng:Double = 0.0
    var lat:Double= 0.0
    var observations = ArrayList<Observation>()
    var plans = ArrayList<PlanItem>()
    var planned = false
    lateinit var controller: ApplicationController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        controller = applicationContext as ApplicationController
        observations = controller.getObservations()
        plans = controller.getPlans()
        getCurrentLocation()
        loadMap()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_4
        bnv.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    controller.goToMain()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> {
                    controller.goToJournal()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_3 -> {
                    controller.goToPlaning()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_4 -> {

                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_5 -> {
                    controller.goToStats()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
    override fun onResume() {
        super.onResume()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_4
    }
    fun loadMap(){
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        if(planned){
            for(i in 0 until plans.size){
                val location = LatLng(plans[i].lat, plans[i].lng)
                mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(location).title(plans[i].content))
            }
        }
        else{
            for(i in 0 until observations.size){
                val location = LatLng(observations[i].lat, observations[i].lng)
                mMap.addMarker(MarkerOptions().position(location).title(SimpleDateFormat("yyyy/MM/dd HH:mm")
                        .format(SimpleDateFormat("yyyyMMdd_HHmm")
                                .parse(observations[i].date))+" "+observations[i].animal_type))
            }

        }
        val location = LatLng(lat, lng)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))

    }
    fun getCurrentLocation(){
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var ll: MyLocationListener = MyLocationListener()
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, ll)
            Log.d("GPS Enabled", "GPS Enabled")
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    lat = location.getLatitude()
                    lng = location.getLongitude()
                }
            }



        }
    }
    fun changeMarkers(view: View){
        Log.d("click change markers",planned.toString())
        val btn = findViewById<Button>(R.id.button)
        if(!planned){
            btn.text="Pokaż obserwacje"

        }
        else{
            btn.text="Pokaż planowane"
        }
        planned = !planned
        loadMap()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }


}
