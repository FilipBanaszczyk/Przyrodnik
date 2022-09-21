package com.example.przyrodnik


import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log

import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class VideoRecordingActivity : AppCompatActivity() {
    private var vv: VideoView? = null
    val VIDEO_REQUEST = 101
    var vUri:Uri? = null

    var file_path = ""
    private var permissionToRecordAccepted = false
    val REQUEST_RECORD_VIDEO_PERMISSION = 200
    lateinit var controller:ApplicationController
    private var permissions: Array<String> = arrayOf(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = applicationContext as ApplicationController
        setContentView(R.layout.activity_video_recording)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        capture()
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_VIDEO_PERMISSION)
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_VIDEO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    fun capture() {
        val vi = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (vi.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(vi, VIDEO_REQUEST)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            vUri = data?.data

            if(vUri!=null){
                try {
                    Log.e("videopath", "videopath")
                    val videoAsset = contentResolver.openAssetFileDescriptor(vUri!!, "r")
                    val fis: FileInputStream = videoAsset!!.createInputStream()
                    val root = getExternalFilesDir(Environment.DIRECTORY_MOVIES)

                    val file: File
                    val date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
                    file = File(root, "recv_" + date + ".mp4")

                    file_path = file.path
                    controller.putToDB(file_path, this)
                    Log.e("videopath", "${root?.path}   ${file.path}")
                    val fos = FileOutputStream(file)
                    val buf = ByteArray(1024)
                    var len: Int = 0
                    while (fis.read(buf).also({ len = it }) > 0) {
                        fos.write(buf, 0, len)
                    }
                    fis.close()
                    fos.close()
                } catch (e: Exception) {
                    Log.d("saving video","error")
                    e.printStackTrace()
                }
            }
            goToMain()

        }
        else{
            goToMain()
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        goToMain()
    }
    fun goToMain(){
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
}