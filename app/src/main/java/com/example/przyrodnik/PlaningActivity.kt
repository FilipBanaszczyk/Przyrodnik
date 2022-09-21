package com.example.przyrodnik

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class PlaningActivity : AppCompatActivity() {

    val channel_id = "primary_notification_channel"
    var items = ArrayList<PlanItemHolder>()
    lateinit var item_adapter: PlanItemAdapter
    lateinit var controller: ApplicationController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = applicationContext as ApplicationController
        setContentView(R.layout.activity_planing)
        createNotificationChannel()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_3
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
        val plan_rv = this.findViewById<RecyclerView>(R.id.rv3)
        plan_rv.layoutManager = LinearLayoutManager(this)
        val plans = controller.getNotifications()
        for(plan in plans){
            items.add(PlanItemHolder(plan))
        }
        item_adapter = PlanItemAdapter(items, this, plan_rv)

        plan_rv.adapter = item_adapter


    }
    override fun onResume() {
        super.onResume()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_3
    }
    private fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    fun fabOnClick(view: View){
        Log.d("planing", "fab")
        item_adapter.newNotification()
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_id
            Log.d("planning", "creating notification channel")
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channel_id, name, importance).apply {
                description = "ePrzyrodnik powiadomienia"
            }

            val notificationManager: NotificationManager =
                   getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }




}