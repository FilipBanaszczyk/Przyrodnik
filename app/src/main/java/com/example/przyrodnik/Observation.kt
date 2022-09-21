package com.example.przyrodnik

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable


open class Observation() : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var uris:String =""
    var lat: Double = 0.0
    var lng: Double = 0.0
    var date:String =""
    var animal_type =""

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        uris = parcel.readString().toString()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        date = parcel.readString().toString()
        animal_type = parcel.readString().toString()
    }



}