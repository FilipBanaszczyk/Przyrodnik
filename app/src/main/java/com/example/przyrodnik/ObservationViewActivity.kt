package com.example.przyrodnik


import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ObservationViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var observations: ArrayList<Observation>
    var index = 0
    lateinit var player: SimpleExoPlayer

    lateinit var controller: ApplicationController
    val types = arrayOf("Ssaki", "Ptaki", "Gady", "Płazy", "Ryby",
            "Owady", "Pajęczaki", "Mięczaki", "Skorupiaki", "Wije", "Pierścienice", "Parzydełkowce", "Nieznane")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_view)

        controller = applicationContext as ApplicationController
        val id = intent.extras?.getLong("observation id")
        Log.d("chosen obs id", id.toString())
        val observation = controller.getObservationWithId(id)
        if(controller.from_animals){
            observations = controller.getObservationWithTypeAndFormat(controller.type, getFormat(observation?.uris!!))
            Log.d("observation view", "loaded ${observations.size} observations ${controller.type}")
        }
        else{
            observations = controller.getObservationWithDateAndFormat(controller.day, getFormat(observation?.uris!!))
            Log.d("observation view", "loaded ${observations.size} observations ${controller.day}")
        }

        index = observations.indexOf(observation)



        val image = findViewById<ImageView>(R.id.image)
        val player_view = findViewById<View>(R.id.view2) as PlayerView
        player_view.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {

            override fun onSwipeLeft() {
                Log.d("swipe", "left")
                if (index == 0) {
                    index = observations.size - 1
                } else {
                    index--

                }
                loadMedia()
            }

            override fun onSwipeRight() {
                Log.d("swipe", "right")
                if (index == observations.size - 1) {
                    index = 0
                } else {
                    index++

                }
                loadMedia()
            }
        })
        image.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {


            override fun onSwipeLeft() {
                Log.d("swipe image", "left")

                if (index == 0) {
                    index = observations.size - 1
                } else {
                    index--

                }
                loadMedia()
            }

            override fun onSwipeRight() {
                Log.d("swipe image", "right")

                if (index == observations.size - 1) {
                    index = 0
                } else {
                    index++

                }
                loadMedia()
            }
        })
        loadMedia()



    }
    fun loadMedia(){
        Log.d("loading index", index.toString())
        player = SimpleExoPlayer.Builder(this).build()
        val image = findViewById<ImageView>(R.id.image)
        val note = findViewById<EditText>(R.id.edit_note)
        note.visibility =  View.GONE

        val player_view = findViewById<View>(R.id.view2) as PlayerView
        val uri = Uri.parse(observations[index].uris)
        val format = getFormat(uri.toString())
        when (format){
            "jpg" -> {
                player_view.visibility = View.GONE
                Glide.with(this)
                        .load(Uri.fromFile(File(uri.toString())))
                        .into(image)

            }
            "3gp" -> {
                val icon_id = this.resources.getIdentifier("audio_icon", "drawable", this.packageName)
                player_view.player = player
                val back_id = this.resources.getIdentifier("species_item", "drawable", this.packageName)

                image.background = ActivityCompat.getDrawable(this, back_id)
                image.layoutParams.height = 170
                image.layoutParams.width = 170
                image.setPadding(20, 30, 20, 20)

                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
            "mp4" -> {
                image.visibility = View.GONE
                player_view.player = player
                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
            "txt" ->{

                val file = File(uri.toString())
                note.addTextChangedListener (object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {}
                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {
                       file.writeText(s.toString())
                    }
                })
                player_view.visibility = View.GONE
                image.visibility = View.GONE
                note.visibility = View.VISIBLE
                val text = file.bufferedReader().readText()
                Log.d("load note", text)
                note.setText(text)

            }
            else->{

            }
        }

    }
    fun removeObservation(view: View){
        var del_dialog = android.app.AlertDialog.Builder(this)
        del_dialog.setTitle("Czy usunąć obserwacje?")
        del_dialog.setPositiveButton("Usuń"){ dialog, which ->

            controller.deleteObservation(observations[index].id)
            onBackPressed()
        }
        del_dialog.setNegativeButton("Anuluj",null)
        del_dialog.show()
    }

    fun getFormat(uri: String?):String{
        if(uri!=null){
            Log.d("getformat", uri.subSequence(uri.length - 3, uri.length).toString())
            return uri.subSequence(uri.length - 3, uri.length).toString()
        }
        return ""
    }

    fun showOptions(view: View){

        val dial_adapter = ArrayAdapter(
                applicationContext, R.layout.simple_custom_spinner_item, types)


        val dialog_view = LayoutInflater.from(this).inflate(R.layout.observation_dialog, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialog_view)
                .setTitle("Opcje")
        val dialog = builder.show()
        val btn = dialog_view.findViewById<Button>(R.id.btn)
        val data_btn = dialog_view.findViewById<Button>(R.id.button7)
        val tv_date = dialog_view.findViewById<TextView>(R.id.tv_date)

        tv_date.text = "Data: "+ SimpleDateFormat("yyyy/MM/dd HH:mm").format(SimpleDateFormat("yyyyMMdd_HHmm").parse(observations[index]?.date))



        val spinner = dialog_view.findViewById<Spinner>(R.id.spinner)
        spinner.setAdapter(dial_adapter)
        if(observations[index]?.animal_type!=""){
            spinner.setSelection(findType())
        }



        spinner.setOnItemSelectedListener(this)
        btn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            controller.showObservationOnMap(observations[index])

        })
        data_btn.setOnClickListener(View.OnClickListener {
            if(getFormat(observations[index].uris)=="jpg" || getFormat(observations[index].uris)=="mp4"){
                controller.goToAnimalSearch(observations[index].id, true)
            }
            else{
                controller.goToAnimalSearch(observations[index].id, false)
            }

        })

    }
    private fun findType():Int{
        for (i in 0 until types.size){
            if(types[i]==observations[index]?.animal_type){
                return i
            }
        }
        return 0
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        controller.updateObservation(observations[index]!!, types[p2])
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(controller.from_animals){
            controller.goToAnimalType("")
        }
        else{
            controller.goToJournalDay("")
        }


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}