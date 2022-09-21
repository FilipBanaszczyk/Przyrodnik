package com.example.przyrodnik

import android.Manifest
import android.app.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class ImageCapturingActivity : AppCompatActivity(){


    val IMAGE_REQUEST = 1
    var iUri: Uri? = null
    lateinit var currentPhotoPath: String

    private var permissionToRecordAccepted = false
    val REQUEST_RECORD_PHOTO_PERMISSION = 200
    lateinit var controller:ApplicationController
    private var permissions: Array<String> = arrayOf(Manifest.permission.CAMERA)

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capturing)
        controller = applicationContext as ApplicationController
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_PHOTO_PERMISSION)
        if(savedInstanceState==null){
            capture()
        }
        else{

            controller.goToMain()
        }


    }


    private fun capture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {

                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    iUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, iUri)
                    startActivityForResult(takePictureIntent, IMAGE_REQUEST)
                }
            }
        }
    }
    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
        val myDir = File("$root/eprzyrodnik_images")
        myDir.mkdirs()

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        val fname = "$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        val storageDir: File? = getExternalFilesDir(DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply { currentPhotoPath = absolutePath
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("uri", iUri.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString("uri", iUri.toString())
                apply()

            }
            try{
                Log.d("try", resultCode.toString())
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, iUri)
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
                saveImageToExternalStorage(bitmap)
                controller.putToDB(currentPhotoPath, this)

                controller.goToMain()
            }
            catch (e: Exception){
                controller.goToMain()
            }



        }
        else{
            Log.d("on imageresult", currentPhotoPath)
            val file: File = File(currentPhotoPath)
            file.delete()
            controller.goToMain()
        }
    }




    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }


}