package com.example.przyrodnik

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PlanMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var lng:Double = 0.0
    var lat:Double= 0.0
    var title: String = ""
    var set_location = true
    lateinit var plan:PlanItem
    lateinit var controller: ApplicationController
    lateinit var latLng: LatLng
    var observations = ArrayList<Observation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_map)
        controller = applicationContext as ApplicationController
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        plan =intent.extras?.get("plan") as PlanItem
        if(plan.lat == 0.0 && plan.lng == 0.0){
            set_location = false
            getCurrentLocation()
        }
        else{
            lng = plan.lng
            lat = plan.lat
        }
        observations = controller.getObservations()
        latLng = LatLng(lat, lng)
        title = SimpleDateFormat("yyyy/MM/dd HH:mm").format(plan.time)+" " + plan.content
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(lat, lng)
        val marker = MarkerOptions()
        marker.position(location)
        marker.draggable(true)

        mMap.addMarker(marker.title(title))
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                latLng = marker.position

            }
        })

        for(i in 0 until observations.size){
            val location = LatLng(observations[i].lat, observations[i].lng)
            val ob_markers = MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(location).title(SimpleDateFormat("yyyy/MM/dd HH:mm")
                    .format(SimpleDateFormat("yyyyMMdd_HHmm").parse(observations[i].date))+" "+observations[i].animal_type)

            mMap.addMarker(ob_markers)

        }

    }
    fun getCurrentLocation(){
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var ll: MyLocationListener? = MyLocationListener()
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, ll!!)
            Log.d("GPS Enabled", "GPS Enabled")
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    lat = location.getLatitude()
                    lng = location.getLongitude()
                }
            }
            lm.removeUpdates(ll)
            ll = null



        }
    }
    fun saveLocation(view: View){
        controller.changePlanLocalization(plan, latLng)
        controller.goToPlaning()

    }
    fun cancel(view: View){
        controller.goToPlaning()
    }
}