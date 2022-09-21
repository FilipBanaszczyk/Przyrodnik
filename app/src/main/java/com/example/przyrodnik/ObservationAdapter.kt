package com.example.przyrodnik

import android.app.AlertDialog
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File


class ObservationAdapter(val parent: JournalItemActivity, val obs_list: ArrayList<Observation>, var mode: Boolean) : RecyclerView.Adapter<ObservationAdapter.ViewHolder>()  {

    val ctx = parent as Context
    val controller = ctx.applicationContext as ApplicationController

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val item = view.findViewById<ConstraintLayout>(R.id.obs_item)
        val select_switch = view.findViewById<RadioButton>(R.id.select)
        val border = view.findViewById<ImageView>(R.id.border)
        val duration = view.findViewById<TextView>(R.id.duration)
        var image = view.findViewById<ImageView>(R.id.image)
        val dialog = AlertDialog.Builder(ctx)
        var state = false

        init {

            if(mode){
                Log.d("switch mode on", mode.toString())
                select_switch.visibility = View.VISIBLE
            }
            else{
                Log.d("switch mode off", mode.toString())
                select_switch.visibility = View.INVISIBLE
            }
            dialog.setTitle("Czy na pewno usunąć obserwację?")
            dialog.setPositiveButton("Usuń"){ dialog, which ->

                controller.deleteObservation(obs_list[adapterPosition].id)
                obs_list.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            dialog.setNegativeButton("Anuluj"){ dialog, which ->
                dialog.dismiss()
            }
            image.setOnLongClickListener {
                chosen_id = obs_list[adapterPosition].id
                parent.turnSelectMode()

                state = true
                controller.selected_obs.add(obs_list[adapterPosition].id)
                notifyItemChanged(adapterPosition)
                //dialog.show()
                true
            }
            image.setOnClickListener(View.OnClickListener {
                if(mode){
                    if(!select_switch.isChecked){
                        Log.d("select ob","$adapterPosition  ${!select_switch.isChecked}")
                        state = true
                        border.visibility = View.VISIBLE
                        select_switch.isChecked = true
                        Log.d("select id", "${obs_list[adapterPosition].id}   ${controller.selected_obs.contains(obs_list[adapterPosition].id)}")
                        if(!controller.selected_obs.contains(obs_list[adapterPosition].id)){

                            for(i in 0 until controller.selected_obs.size){
                                Log.d("controller selected", controller.selected_obs[i].toString())
                            }
                            controller.selected_obs.add(obs_list[adapterPosition].id)
                        }



                    }
                    else{
                        Log.d("unselect ob","${obs_list[adapterPosition].id}  ${!select_switch.isChecked}")
                        state = false
                        select_switch.isChecked = false
                        border.visibility = View.INVISIBLE
                        Log.d("unselect", "${controller.selected_obs.size}")
                        controller.selected_obs.remove(obs_list[adapterPosition].id)
                        Log.d("unselect", "${controller.selected_obs.size}")
                    }

                }
                else{
                    controller.goToObservationView(obs_list[adapterPosition])
                }

            })
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(
            R.layout.adapter_observation, parent, false
        )
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(obs_list[position].id == chosen_id && mode){
            //Log.d("chosen id bind", chosen_id.toString())
            holder.border.visibility = View.VISIBLE
            holder.select_switch.isChecked = true
        }

        if(getFormat(obs_list[position].uris)=="jpg"){

            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide
                .with(ctx)
                .asBitmap()
                .load(Uri.fromFile(File(obs_list[position].uris)))
                .into(holder.image)


        }
        else if(getFormat(obs_list[position].uris)=="3gp"){
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(ctx, Uri.parse(obs_list[position].uris))
            var milis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)


            retriever.release()

            val icon_id = ctx.resources.getIdentifier("audio_icon", "drawable", ctx.packageName)
            val back_id = ctx.resources.getIdentifier("species_item", "drawable", ctx.packageName)


            holder.image.background = ActivityCompat.getDrawable(ctx, back_id)
            holder.image.layoutParams.height = 170
            holder.image.layoutParams.width = 170
            holder.image.setPadding(20,30,20,20)



            holder.duration.text = getTime(milis)
        }
        else if(getFormat(obs_list[position].uris)=="mp4"){
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(ctx, Uri.parse(obs_list[position].uris))
            val milis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP

            retriever.release()

            Glide
                    .with(ctx)
                    .asBitmap()
                    .load(Uri.fromFile(File(obs_list[position].uris)))
                    .into(holder.image)
            holder.duration.text = getTime(milis)
        }
        else if(getFormat(obs_list[position].uris)=="txt"){

            val file = File(obs_list[position].uris)
            val text = file.bufferedReader().readText()
            if(text.length>=13){
                holder.duration.text = text.subSequence(0,12).toString()+"..."
            }
            else{
                holder.duration.text = text+"..."
            }

            //Log.d("obs adapter", obs_list[position].uris)
            val icon_id = ctx.resources.getIdentifier("notes_icon", "drawable", ctx.packageName)
            val back_id = ctx.resources.getIdentifier("species_item", "drawable", ctx.packageName)
            holder.image.setImageDrawable(ctx.resources.getDrawable(icon_id))
            holder.image.background = ActivityCompat.getDrawable(ctx, back_id)
            holder.image.layoutParams.height = 170
            holder.image.layoutParams.width = 170
            holder.image.setPadding(20,30,20,20)

        }

    }
    private fun getTime(milis:String?):String{
        var result = ""
        if(milis!=null){

            try{
                val time = milis?.toLong()
                if((time/60000)<9){
                    result+="0${(time/60000)}"
                }
                else{
                    result+="${(time/60000)}"
                }
                result+=":"
                if(((time/1000)%60)<9){
                    result+="0${(time/1000)%60}"
                }
                else{
                    result+="${(time/1000)%60}"
                }

            }
            catch (e: Exception){
                Log.d("Obs adapter get time","time not correct")
            }


        }

        return result
    }
    fun getFormat(uri: String):String{
        //Log.d("getformat", uri.subSequence(uri.length-3,uri.length).toString())
        return uri.subSequence(uri.length-3,uri.length).toString()
    }
    companion object{
        var chosen_id = -1L
    }




    override fun getItemCount(): Int {
        return obs_list.size
    }



}
