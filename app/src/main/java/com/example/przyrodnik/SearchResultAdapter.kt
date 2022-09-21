package com.example.przyrodnik

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.util.ArrayList

class SearchResultAdapter(private var result_list: ArrayList<ParsedItem>, var ctx: Context):
        RecyclerView.Adapter<SearchResultAdapter.ItemVH>() {
    interface OnClickListener
    {
        fun onItemClick(index: Int)
    }
    private var onClickListener: OnClickListener? = null



    val controller = ctx.applicationContext as ApplicationController
    inner class ItemVH(view: View) : RecyclerView.ViewHolder(view){
        val item = view.findViewById<LinearLayout>(R.id.item)
        val image = view.findViewById<ImageView>(R.id.image)
        val name = view.findViewById<TextView>(R.id.tv)
        fun bindOnClickListener(onClickListener: OnClickListener?, index: Int)
        {
            item.setOnClickListener{view -> onClickListener?.onItemClick(index)}

        }



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultAdapter.ItemVH {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(
                R.layout.adapter_search_result, parent, false
        )
        //Log.d("adapter", "created")
        return  ItemVH(view)
    }

    override fun onBindViewHolder(holder: SearchResultAdapter.ItemVH, position: Int) {
        holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide
                .with(ctx)
                .asBitmap()
                .load(result_list[position].image_url)
                .into(holder.image)
        holder.name.text = result_list[position].name
        holder.bindOnClickListener(onClickListener,position)
    }

    override fun getItemCount(): Int {
        return result_list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

}