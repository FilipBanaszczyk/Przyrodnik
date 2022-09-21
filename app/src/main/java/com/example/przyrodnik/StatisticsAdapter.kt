package com.example.przyrodnik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatisticsAdapter(ctx: Context,var  item_list: ArrayList<StatisticItem>): RecyclerView.Adapter<StatisticsAdapter.ItemVH>() {


    val controller = ctx.applicationContext as ApplicationController
    inner class ItemVH(view: View): RecyclerView.ViewHolder(view){
        val tv_cat = view.findViewById<TextView>(R.id.tv_cat)
        val tv_photo = view.findViewById<TextView>(R.id.tv_photo)
        val tv_video = view.findViewById<TextView>(R.id.tv_video)
        val tv_audio = view.findViewById<TextView>(R.id.tv_audio)
        val tv_notes = view.findViewById<TextView>(R.id.tv_notes)
        val tv_sum = view.findViewById<TextView>(R.id.tv_sum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(
                R.layout.adapter_statistics, parent, false
        )

        return  ItemVH(view)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.tv_cat.text = item_list[position].cat
        if(item_list[position].photo!=0){
            holder.tv_photo.text = item_list[position].photo.toString()
        }
        else{
            holder.tv_photo.text = ""
        }
        if(item_list[position].video!=0){
            holder.tv_video.text = item_list[position].video.toString()
        }
        else{
            holder.tv_video.text = ""
        }
        if(item_list[position].audio!=0){
            holder.tv_audio.text = item_list[position].audio.toString()
        }
        else{
            holder.tv_audio.text = ""
        }
        if(item_list[position].notes!=0){
            holder.tv_notes.text = item_list[position].notes.toString()
        }
        else{
            holder.tv_notes.text = ""
        }
        holder.tv_sum.text = item_list[position].sum.toString()
    }

    override fun getItemCount(): Int {
        return  item_list.size
    }
}