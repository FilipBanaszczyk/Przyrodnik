package com.example.przyrodnik

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainMenuActivity : AppCompatActivity() {


    lateinit var  controller: ApplicationController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_1
        controller = applicationContext as ApplicationController
        bnv.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {

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
                    controller.goToMap()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_5 -> {
                    controller.goToStats()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WAKE_LOCK
        )

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }

    }
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onResume() {
        super.onResume()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_1
    }


    fun goToVideoRecording(view: View?) {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA


        )

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
        else{
            val play = Intent(this, VideoRecordingActivity::class.java)
            //play.putExtra("uri", vUri.toString())
            startActivity(play)
        }
        if (hasPermissions(this, PERMISSIONS)) {
            val play = Intent(this, VideoRecordingActivity::class.java)
            //play.putExtra("uri", vUri.toString())
            startActivity(play)
        }

    }
    fun goToTakingPhoto(view: View?) {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA


        )
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)

        if (!hasPermissions(this, PERMISSIONS)) {

        }
        else{
            val play = Intent(this, ImageCapturingActivity::class.java)
            //play.putExtra("uri", vUri.toString())
            startActivity(play)
        }
        if (hasPermissions(this, PERMISSIONS)) {
            val play = Intent(this, ImageCapturingActivity::class.java)
            //play.putExtra("uri", vUri.toString())
            startActivity(play)
        }

    }
    fun goToAudioRecording(view: View?) {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO


        )
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
        else{
            val play = Intent(this, AudioRecordingActivity::class.java)

            startActivity(play)
        }
        if (hasPermissions(this, PERMISSIONS)) {
            val play = Intent(this, AudioRecordingActivity::class.java)

            startActivity(play)
        }

    }
    fun goToNoteEditor(view: View){
        controller.goToNote()
    }



}