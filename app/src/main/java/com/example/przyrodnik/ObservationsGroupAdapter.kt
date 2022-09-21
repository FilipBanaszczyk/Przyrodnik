package com.example.przyrodnik

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ObservationsGroupAdapter(val obs_list: ArrayList<Observation>, factor: String, val parent: JournalItemActivity):  RecyclerView.Adapter<ObservationsGroupAdapter.ViewHolder>() {

    val ctx = parent as Context
    val controller = ctx.applicationContext as ApplicationController
    lateinit var map: HashMap<String, ArrayList<Observation>>
    var select = false

    val keys =  ArrayList<String>()

    init{
        Log.d("init obsgroup", factor)

        if(factor=="type"){
            map = controller.groupByDate(obs_list)
            for(key in map.keys){
                keys.add(key)

            }
            keys.sort()
            keys.reverse()
            for( key in keys){
                Log.d("sorted keys type", key)
            }
        }
        else if (factor=="day"){
            map = controller.groupByType(obs_list)
            for(key in map.keys){
                keys.add(key)

            }
            for( key in keys){
                Log.d("sorted keys day", key)
            }
            keys.sortWith(compareBy({it}))
            keys.reverse()
            keys.sortWith(compareBy({ map[it]?.size }))
            keys.reverse()
            for( key in keys){
                Log.d("sorted keys day", map[key]?.size.toString())
            }
        }


    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val group_name = view.findViewById<TextView>(R.id.tv_group)
        val rv = view.findViewById<RecyclerView>(R.id.rv)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationsGroupAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(
                R.layout.adapter_observations_group, parent, false
        )
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObservationsGroupAdapter.ViewHolder, position: Int) {
        if(position>=0 && position<keys.size){
            holder.group_name.text = keys[position]
            //holder.rv.addItemDecoration(GridSpacingItemDecoration(3, 7, true))
            Log.d("obs group dapdater", " bind $select")
            val adapter = ObservationAdapter(parent, map[keys[position]]!!, select)
            holder.rv.adapter = adapter
            holder.rv.layoutManager = GridLayoutManager(ctx,3)
        }



    }
    fun turnSelectMode(state: Boolean){

        select = state
        Log.d("obs group dapdater", " select mode turn $select")

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return obs_list.size
    }
}