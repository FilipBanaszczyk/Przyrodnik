package com.example.przyrodnik

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SummaryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var controller: ApplicationController
    val types_arr = arrayListOf("Nieznane", "Ssaki", "Ptaki", "Gady", "Płazy", "Ryby",
            "Owady", "Pajęczaki", "Mięczaki", "Skorupiaki", "Wije", "Pierścienice", "Parzydełkowce", "Koralowce")
    var item_list = ArrayList<StatisticItem>()
    var date_pick = false
    lateinit var adapter: StatisticsAdapter
    lateinit var alert: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        controller = applicationContext as ApplicationController
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_5
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
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        alert = findViewById(R.id.tv_alert)
        val start_date = findViewById<TextView>(R.id.start)
        val end_date = findViewById<TextView>(R.id.end)
        start_date.text = SimpleDateFormat("yyyy-MM-dd").format(controller.getEarliestDate())
        end_date.text = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val rv = findViewById<RecyclerView>(R.id.rv)

        adapter = StatisticsAdapter(this, item_list)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
        getStatisticsItems()

    }
    override fun onResume() {
        super.onResume()
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_5
    }
    fun getStatisticsItems(){
        val start_date = SimpleDateFormat("yyyy-MM-dd").parse(findViewById<TextView>(R.id.start).text.toString())
        val end_date = SimpleDateFormat("yyyy-MM-dd").parse(findViewById<TextView>(R.id.end).text.toString())
        end_date.hours = 23
        end_date.minutes = 59
        item_list = controller.getObservationsFromRange(start_date, end_date)
        if(item_list.size==0){
            val no_stats = findViewById<TextView>(R.id.nostat)
            no_stats.visibility = View.VISIBLE
        }
        else{
            val no_stats = findViewById<TextView>(R.id.nostat)
            no_stats.visibility = View.GONE
        }
        Log.d("get stats", item_list.size.toString())
        adapter.item_list = item_list
        adapter.notifyDataSetChanged()

    }

    fun showDatePickerDialog() {
        if(date_pick){
            val end_date = findViewById<TextView>(R.id.end)
            val date = SimpleDateFormat("yyyy-MM-dd").parse(end_date.text.toString())
            val calendar = Calendar.getInstance()
            calendar.time = date
            val datePickerDialog = DatePickerDialog(
                    this,
                    this,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }
        else{
            val start_date = findViewById<TextView>(R.id.start)
            val date = SimpleDateFormat("yyyy-MM-dd").parse(start_date.text.toString())
            val calendar = Calendar.getInstance()
            calendar.time = date
            val datePickerDialog = DatePickerDialog(
                    this,
                    this,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        if(date_pick){
            val end_date = findViewById<TextView>(R.id.end)

            end_date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(p1-1900,p2,p3))
        }
        else{
            val start_date = findViewById<TextView>(R.id.start)
            start_date.text = SimpleDateFormat("yyyy-MM-dd").format(Date(p1-1900,p2,p3))
        }
        val end_date = findViewById<TextView>(R.id.end)
        val end = SimpleDateFormat("yyyy-MM-dd").parse(end_date.text.toString())
        val start_date = findViewById<TextView>(R.id.start)
        val start = SimpleDateFormat("yyyy-MM-dd").parse(start_date.text.toString())
        if(end<start){
            alert.visibility = View.VISIBLE
        }
        else{
            alert.visibility = View.INVISIBLE
        }
        getStatisticsItems()

    }
    fun fromDate(view : View){
        date_pick = false
        showDatePickerDialog()

    }
    fun toDate(view : View){
        date_pick = true
        showDatePickerDialog()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }
}
