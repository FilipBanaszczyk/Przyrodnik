package com.example.przyrodnik

import android.location.Location
import android.location.LocationListener
import android.util.Log


class MyLocationListener: LocationListener {
    override fun onLocationChanged(p0: Location) {
        Log.d("Location changed", p0.latitude.toString()+"  "+p0.longitude.toString())
    }



}