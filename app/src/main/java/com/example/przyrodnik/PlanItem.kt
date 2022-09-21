package com.example.przyrodnik

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Parcel
import android.os.Parcelable
import androidx.core.app.ActivityCompat
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class PlanItem() : RealmObject(), Parcelable {

    @PrimaryKey
    var id: Long = 0
    var content:String = ""
    var desc:String = ""
    var time:Date = Date()
    var state:Boolean= false
    var lat: Double = 0.0
    var lng: Double = 0.0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        content = parcel.readString().toString()
        state = parcel.readByte() != 0.toByte()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(content)
        parcel.writeByte(if (state) 1 else 0)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlanItem> {
        override fun createFromParcel(parcel: Parcel): PlanItem {
            return PlanItem(parcel)
        }

        override fun newArray(size: Int): Array<PlanItem?> {
            return arrayOfNulls(size)
        }
    }


}