package com.example.przyrodnik

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    lateinit var  controller: ApplicationController
    lateinit var edit: EditText
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmSS").format(Date())

    var overwrite = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        controller = applicationContext as ApplicationController
        edit = findViewById(R.id.editText)
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


    }
    @SuppressLint("ShowToast")
    fun saveNote(view : View){
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, "note_" + timeStamp + ".txt")
        val file_path = file.path
        if(!overwrite){
            file.writeText(edit.text.toString())
            controller.putToDB(file_path, this)
            Toast.makeText(controller,"Zapisano notatkę", Toast.LENGTH_LONG).show()
            overwrite = true
        }
        else{
            file.writeText(edit.text.toString())
            Toast.makeText(controller,"Nadpisano notatkę", Toast.LENGTH_LONG).show()
        }

    }
    fun goToMain(view : View){

        controller.goToMain()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }
}