package com.example.przyrodnik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat


class JournalItemAdapter(ctx: Context, private var list :ArrayList<String>, val  factor: String):
    RecyclerView.Adapter<JournalItemAdapter.ViewHolder>() {


    val controller = ctx.applicationContext as ApplicationController



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val item = view.findViewById<LinearLayout>(R.id.day_item)
        val tv_photo= view.findViewById<TextView>(R.id.tv_photo)
        val tv_video= view.findViewById<TextView>(R.id.tv_video)
        val tv_audio= view.findViewById<TextView>(R.id.tv_audio)
        val tv_notes= view.findViewById<TextView>(R.id.tv_notes)
        val i_photo= view.findViewById<ImageView>(R.id.i_photo)
        val i_video= view.findViewById<ImageView>(R.id.i_video)
        val i_audio= view.findViewById<ImageView>(R.id.i_audio)
        val i_notes= view.findViewById<ImageView>(R.id.i_notes)

        var tv_date = view.findViewById<TextView>(R.id.tv_date)

        init {

            item.setOnClickListener(View.OnClickListener {
                if(factor=="Dziennik"){
                    controller.goToJournalDay(SimpleDateFormat("yyyyMMdd").format(SimpleDateFormat("yyyy-MM-dd").parse(list[adapterPosition])))
                }
                else if(factor=="Zwierzęta"){
                    controller.goToAnimalType(list[adapterPosition])
                }


            })

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.adapter_journal
            , parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = list[position]
        holder.tv_date.text = content
       // Log.d("Journal adapter", "$factor   $content")
        if(factor=="Dziennik") {
            val date = SimpleDateFormat("yyyyMMdd").format(SimpleDateFormat("yyyy-MM-dd").parse(content))
            val p = controller.countObservationsByDateAndFormat(date, "jpg").toString()
            val v = controller.countObservationsByDateAndFormat(date, "mp4").toString()
            val a = controller.countObservationsByDateAndFormat(date, "3gp").toString()
            val n = controller.countObservationsByDateAndFormat(date, "txt").toString()
            if(p=="0"){
                holder.i_photo.visibility = View.GONE
                holder.tv_photo.visibility = View.GONE
            }
            else{
                holder.i_photo.visibility = View.VISIBLE
                holder.tv_photo.visibility = View.VISIBLE
                holder.tv_photo.text = p 
            }
            if(v=="0"){
                holder.i_video.visibility = View.GONE
                holder.tv_video.visibility = View.GONE
            }
            else{
                holder.i_video.visibility =View.VISIBLE
                holder.tv_video.visibility = View.VISIBLE
                holder.tv_video.text = v
            }
            if(a=="0"){
                holder.i_audio.visibility = View.GONE
                holder.tv_audio.visibility = View.GONE
            }
            else{
                holder.tv_audio.text = a
                holder.i_audio.visibility =View.VISIBLE
                holder.tv_audio.visibility = View.VISIBLE
            }
            if(n=="0"){
                holder.i_notes.visibility = View.GONE
                holder.tv_notes.visibility = View.GONE
            }
            else{
                holder.tv_notes.text = n
                holder.i_notes.visibility =View.VISIBLE
                holder.tv_notes.visibility = View.VISIBLE
            }
            

        }
        else if(factor == "Zwierzęta"){

            val p = controller.countObservationsByTypeAndFormat(content, "jpg").toString()
            val v = controller.countObservationsByTypeAndFormat(content, "mp4").toString()
            val a = controller.countObservationsByTypeAndFormat(content, "3gp").toString()
            val n = controller.countObservationsByTypeAndFormat(content, "txt").toString()
            //Log.d("zwierzeta adapter", "$content  $p")
            if(p=="0"){
                holder.i_photo.visibility = View.GONE
                holder.tv_photo.visibility = View.GONE
            }
            else{
                holder.i_photo.visibility = View.VISIBLE
                holder.tv_photo.visibility = View.VISIBLE
                holder.tv_photo.text = p
            }
            if(v=="0"){
                holder.i_video.visibility = View.GONE
                holder.tv_video.visibility = View.GONE
            }
            else{
                holder.i_video.visibility =View.VISIBLE
                holder.tv_video.visibility = View.VISIBLE
                holder.tv_video.text = v
            }
            if(a=="0"){
                holder.i_audio.visibility = View.GONE
                holder.tv_audio.visibility = View.GONE
            }
            else{
                holder.tv_audio.text = a
                holder.i_audio.visibility =View.VISIBLE
                holder.tv_audio.visibility = View.VISIBLE
            }
            if(n=="0"){
                holder.i_notes.visibility = View.GONE
                holder.tv_notes.visibility = View.GONE
            }
            else{
                holder.tv_notes.text = n
                holder.i_notes.visibility =View.VISIBLE
                holder.tv_notes.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }




}