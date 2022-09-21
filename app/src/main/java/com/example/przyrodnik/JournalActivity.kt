package com.example.przyrodnik

import android.content.Intent
import android.os.Bundle
import android.os.Environment.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import io.realm.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class JournalActivity : AppCompatActivity() {
    lateinit var tabs: TabLayout
    lateinit var pager: ViewPager

    private var obs_list  = ArrayList<ArrayList<String>>()

    lateinit var date_list: ArrayList<String>


    lateinit var controller: ApplicationController
    val types_arr = arrayListOf("Nieznane", "Ssaki", "Ptaki", "Gady", "Płazy", "Ryby",
        "Owady", "Pajęczaki", "Mięczaki", "Skorupiaki", "Wije", "Pierścienice", "Parzydełkowce", "Koralowce")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)
        controller = applicationContext as ApplicationController
        tabs = findViewById(R.id.tabs)
        pager = findViewById(R.id.pager)
        val tab_titles = ArrayList<String>()
        tab_titles.add("Dziennik")
        tab_titles.add("Zwierzęta")

        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_2
        bnv.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    controller.goToMain()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> {

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

        date_list = controller.countDateObservations()
        date_list.reverse()
        obs_list.add(date_list)
        obs_list.add(types_arr)
        prepareViewPager(tab_titles)
        tabs.setupWithViewPager(pager)


    }

    override fun onResume() {
        super.onResume()
        //val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        //bnv.selectedItemId = R.id.page_2
    }
    fun prepareViewPager(list: ArrayList<String>){
        val adapter = TabAdapter(supportFragmentManager)

        for(i in 0 until list.size){
            val fragment = JournalFragment.newInstance(obs_list[i],list[i])
            adapter.addFragment(fragment, list[i])


        }
        pager.adapter = adapter
    }
    inner class TabAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){
        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        fun addFragment(f: Fragment, t: String){
            fragments.add(f)
            titles.add(t)
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller.goToMain()
    }


}