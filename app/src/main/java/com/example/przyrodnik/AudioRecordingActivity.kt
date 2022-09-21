package com.example.przyrodnik
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AudioRecordingActivity : AppCompatActivity() {


    val LOG_TAG = "AudioRecordTest"
    val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var fileName: String = ""

    private var recorder: MediaRecorder? = null

    lateinit var record_button: Button
    lateinit var play_button: Button
    private var player: MediaPlayer? = null
    var mStartPlaying = true
    var mStartRecording = true
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    lateinit var controller: ApplicationController
    lateinit var tv_time : TextView
    var sec_timer = 0
    var timer = object: CountDownTimer(3600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            sec_timer+=1000

            val time_string=String.format("%02d:%02d", (sec_timer/1000) / 60, (sec_timer/1000) % 60)
            tv_time.text=time_string
        }

        override fun onFinish() {
            tv_time.text="1h"
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
        sec_timer = 0
        timer.start()
    } else {
        timer.cancel()
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        val root = getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()
        val myDir = File("$root")
        myDir.mkdirs()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmSS").format(Date())
        fileName = "$root/arec_$timeStamp.3gp"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        Toast.makeText(controller,"Zapisano nagranie audio", Toast.LENGTH_LONG).show()
        controller.putToDB(fileName, this)
        recorder = null
    }


    fun record(view: View){
        onRecord(mStartRecording)
        record_button.text = when (mStartRecording) {
            true -> "Zatrzymaj nagrywanie"
            false -> "Nagrywaj"
        }
        mStartRecording = !mStartRecording
    }
    fun play(view: View){
        onPlay(mStartPlaying)
        play_button.text = when (mStartPlaying) {
            true -> "Zatrzymaj odtwarzanie"
            false -> "Odtwórz"
        }
        mStartPlaying = !mStartPlaying
    }



    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_audio_recording)
        record_button = findViewById(R.id.button1)
        play_button = findViewById(R.id.button2)
        controller = applicationContext as ApplicationController
        tv_time = findViewById<TextView>(R.id.tv_time)
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_1
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



        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)


    }



    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }
}

