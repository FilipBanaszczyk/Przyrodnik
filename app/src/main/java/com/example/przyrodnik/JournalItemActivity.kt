package com.example.przyrodnik

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat


class JournalItemActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var tabs: TabLayout
    lateinit var pager: ViewPager
    private var obs_list  = ArrayList<ArrayList<Observation>>()

    lateinit var controller: ApplicationController
    var photo_list = ArrayList<Observation>()
    var video_list = ArrayList<Observation>()
    var audio_list = ArrayList<Observation>()
    var notes_list = ArrayList<Observation>()
    lateinit var factor:String
    lateinit var  type: String
    var page = 0
    val tab_titles = ArrayList<String>()
    val types = arrayOf("Ssaki", "Ptaki", "Gady", "Płazy", "Ryby",
            "Owady", "Pajęczaki", "Mięczaki", "Skorupiaki", "Wije", "Pierścienice", "Parzydełkowce", "Nieznane")
    var select_mode = false
    lateinit var fragments: ArrayList<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tab_titles.clear()
        setContentView(R.layout.activity_journal_day)
        controller = applicationContext as ApplicationController

        controller.selected_obs.clear()
        Log.d("jorunal day on create",  controller.selected_obs.size.toString())
        tabs = findViewById(R.id.tabs)
        pager = findViewById(R.id.pager)

        ObservationAdapter.chosen_id = -1
        loadData()

        tab_titles.add("Zdjęcia")
        tab_titles.add("Wideo")
        tab_titles.add("Audio")
        tab_titles.add("Notatki")
        tabs.removeAllTabs()
        prepareViewPager(tab_titles)
        tabs.setupWithViewPager(pager)
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnv.selectedItemId = R.id.page_2
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


    fun loadData(){
        obs_list.clear()

        factor = intent.extras?.getString("factor").toString()

        if(factor.equals("day")){
            val day = controller.day
           // Log.d("Loading data day ", day)
            val tv_date = findViewById<TextView>(R.id.tv_date)

            if(day!=null){
                tv_date.text = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd").parse(day))
                photo_list = controller.getObservationWithDateAndFormat(day, "jpg")
                video_list = controller.getObservationWithDateAndFormat(day, "mp4")
                audio_list = controller.getObservationWithDateAndFormat(day, "3gp")
                notes_list = controller.getObservationWithDateAndFormat(day, "txt")

            }

        }
        else if (factor.equals("type")){
            val type = controller.type
            //Log.d("Loading data type ", type)

            val tv_date = findViewById<TextView>(R.id.tv_date)

            if(type!=null){
                tv_date.text = "Obserwacje: $type"
                photo_list = controller.getObservationWithTypeAndFormat(type, "jpg")
                video_list = controller.getObservationWithTypeAndFormat(type, "mp4")
                audio_list = controller.getObservationWithTypeAndFormat(type, "3gp")
                notes_list = controller.getObservationWithTypeAndFormat(type, "txt")

            }

        }
        Log.d("observations loaded","${photo_list.size}  ${video_list.size}  ${audio_list.size}  ${notes_list.size}")
        photo_list.reverse()
        video_list.reverse()
        audio_list.reverse()
        notes_list.reverse()

        obs_list.add(photo_list)
        obs_list.add(video_list)
        obs_list.add(audio_list)
        obs_list.add(notes_list)

    }
    fun prepareViewPager(list: ArrayList<String>){

        val adapter = TabAdapter(supportFragmentManager)
       // Log.d("prepare view pager", list.size.toString())
        for(i in 0 until list.size){
            val arr = LongArray(obs_list[i].size)
            for(j in 0 .. obs_list[i].size){
                arr[j] = obs_list[i][j].id
            }
            val fragment = ObservationsFragment.newInstance(arr, factor)
            adapter.addFragment(fragment, list[i])

        }
        fragments = adapter.fragments
        pager.adapter = adapter
        pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                page = position
                Log.d("turn on page change", position.toString())
                controller.selected_obs.clear()
                select_mode = true
                turnSelectMode()

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

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

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE

        }


    }
    fun turnSelectMode(){
        Log.d("turn", select_mode.toString())
        if(!select_mode){
            val fr = fragments[pager.currentItem] as ObservationsFragment
            fr.notifyAdapter(!select_mode)
            showButtons()

        }
        else{
            val fr = fragments[pager.currentItem] as ObservationsFragment
            fr.notifyAdapter(!select_mode)
            ObservationAdapter.chosen_id = -1
            hideButtons()

        }
        select_mode=!select_mode
    }
    interface SelectableItems{
        fun notifyAdapter(state: Boolean)
    }
    fun showButtons(){
        val del = findViewById<ImageView>(R.id.btn_del)
        val opt = findViewById<ImageView>(R.id.btn_options)
        del.visibility = View.VISIBLE
        opt.visibility = View.VISIBLE
    }
    fun hideButtons(){
        val del = findViewById<ImageView>(R.id.btn_del)
        val opt = findViewById<ImageView>(R.id.btn_options)
        del.visibility = View.GONE
        opt.visibility = View.GONE

    }
    fun deleteSelected(view: View){
        var del_dialog = android.app.AlertDialog.Builder(this)
        del_dialog.setTitle("Czy usunąć zaznaczone obserwacje?")
        del_dialog.setPositiveButton("Usuń"){ dialog, which ->

            controller.deleteSelected()
            loadData()
            val frag  = fragments[pager.currentItem] as ObservationsFragment
            Log.d("check frag size","${frag.param1.size}   ${pager.currentItem}")
            val arr = LongArray(obs_list[pager.currentItem].size)
            for(j in 0 .. obs_list[pager.currentItem].size){
                arr[j] = obs_list[pager.currentItem][j].id
            }
            frag.param1 = arr
            pager.adapter?.notifyDataSetChanged()
        }
        del_dialog.setNegativeButton("Anuluj",null)
        del_dialog.show()

        turnSelectMode()
        pager.setCurrentItem(page, false)

    }
    fun changeType(view: View){
        val dial_adapter = ArrayAdapter(
                applicationContext, R.layout.simple_custom_spinner_item, types)


        val dialog_view = LayoutInflater.from(this).inflate(R.layout.dialog_change_type, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog_view)
                .setTitle("Opcje")
        val dialog = builder.show()
        val btn = dialog_view.findViewById<Button>(R.id.btn)

        val tv_date = dialog_view.findViewById<TextView>(R.id.tv_date)

        tv_date.text ="Zmień kategorię ${controller.selected_obs.size} obserwacji"


        val spinner = dialog_view.findViewById<Spinner>(R.id.spinner)
        spinner.setAdapter(dial_adapter)

        spinner.setOnItemSelectedListener(this)
        btn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            controller.changeTypeSelected(type)
            loadData()
            val frag  = fragments[pager.currentItem] as ObservationsFragment
            Log.d("check frag size","${frag.param1.size}   ${pager.currentItem}")
            val arr = LongArray(obs_list[pager.currentItem].size)
            for(j in 0 .. obs_list[pager.currentItem].size){
                arr[j] = obs_list[pager.currentItem][j].id
            }
            frag.param1 = arr
            pager.adapter?.notifyDataSetChanged()
            pager.setCurrentItem(page, false)
            turnSelectMode()

        })


    }


    override fun onBackPressed() {
        if(select_mode){

            turnSelectMode()
        }


        else {
            super.onBackPressed()
            select_mode=false
            controller.backFromObservations()
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        type = types[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "journal item")
    }


}