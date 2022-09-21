 package com.example.przyrodnik

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.w3c.dom.Text
import java.io.File


 class AnimalSearchActivity : AppCompatActivity(), SearchResultAdapter.OnClickListener {
    lateinit var edit: EditText
    var link = "https://www.ekologia.pl/wiedza/zwierzeta/"
    var result_list = ArrayList<ParsedItem>()
    val history = ArrayList<String>()
    lateinit var animal_result:AnimalResult
    var animal = false
     val mime = "text/html"
     val encoding = "utf-8"
    var image_id = -1L
    lateinit var adapter: SearchResultAdapter
    lateinit var controller: ApplicationController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_search)
        history.add(link)
        controller = applicationContext as ApplicationController
        image_id = intent.extras?.getLong("compare")?: -1L
        if(image_id!=-1L){
            val iv = findViewById<ImageView>(R.id.image_compare)
            val observation = controller.getObservationWithId(image_id)
            Glide
                    .with(this)
                    .asBitmap()
                    .load(Uri.fromFile(File(observation?.uris)))
                    .into(iv)
        }
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


        val rv = findViewById<RecyclerView>(R.id.rv)
        adapter = SearchResultAdapter(result_list, this)
        adapter.setOnClickListener(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
        val fetcher = AnimalDataFetcher(false,link)
        fetcher.execute()
    }
    fun search(view: View){
        edit = findViewById<EditText>(R.id.edit_name_animal)
        link = "https://www.ekologia.pl/wiedza/zwierzeta/szukaj?search="
        val name = edit.text.toString()
        link += name


        if(name!=""){
            Log.d("search $name",link)
            history.add(link)
            val fetcher = AnimalDataFetcher(false, link)
            fetcher.execute()
        }


    }
    inner class AnimalDataFetcher(val page: Boolean, val link: String): AsyncTask<Void, Void, Void>() {
        val other_pages = ArrayList<String>()
        override fun doInBackground(vararg p0: Void?): Void? {
            if(!page){
                result_list.clear()
            }

            try {
                hideNetError()
                Log.d("fetching", link)
                val doc: Document = Jsoup.connect(link).get()

                val el = doc.select("a.pro_eko_cat") as Elements
                if(el.size==0){
                    try {
                        val name = doc.select("div.comp-title").toString()
                        val image = doc.select("div.atlas-photo").select("img").attr("src")
                        val gallery = doc.select("div.center").toString()
                        val system = doc.select("div.systematyka").toString()


                        val desc = doc.select("div.rw_area_text")
                        val data = desc.toString()

                        animal_result = AnimalResult(name, image, data, gallery, system)
                        //Log.d("fetchdesc", "desc: " + desc[0])

                        animal = true
                    }
                    catch (e: Exception) {
                        Log.d("Data fetcher", "Problem with fetching data from $link")
                        e.printStackTrace()

                    }
                }
                else{

                    val pages = doc.select("a.next") as Elements
                    Log.d("found other page", pages.size.toString())
                    Log.d("items fetched", el.size.toString())
                    for(page in pages){
                        other_pages.add(page.attr("href").toString())
                        Log.d("pagelink", "page:"+page.attr("href").toString())

                    }

                    for (i in 0 until el.size){
                        val image_url = el.select("span.img-tab").select("img").eq(i).attr("src")
                        val name = el.select("span.titlea").eq(i).text()
                        result_list.add(ParsedItem(name, image_url))

                        // Log.d("item added", "name $name   image $image_url")
                    }
                }



            } catch (e: Exception) {
                Log.d("Data fetcher", "Problem with fetching data from $link")
                e.printStackTrace()
                showNetError()

            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            adapter.notifyDataSetChanged()
            if(animal){
                changeView()
            }
            if(other_pages.size>0) fetchOtherPages(other_pages)


        }
    }
     fun showNetError(){
         val error = findViewById<TextView>(R.id.no_net)
         error.visibility = View.VISIBLE
     }
     fun hideNetError(){
         val error = findViewById<TextView>(R.id.no_net)
         error.visibility = View.GONE
     }
     fun fetchOtherPages(pages: ArrayList<String>){
         for(page in pages){
             Log.d("other page",page)
             link = page
             val fetcher = AnimalDataFetcher(true, page)
             fetcher.execute()
         }
     }
     fun prepareHtml(name:String,html: String,html2: String,html3: String):String{
         val result = "<html>\n<head>\n<title>Page Title</title>\n<style>\n" +
                 "a{text-decoration: none; color:#93BE66;font-size: 20px;}\nh1 {color:#CCCCCC;font-size: 30px;}\n" +
                 "h2 {color:#CCCCCC;font-size: 25px;}\nh3 {color:#83AE56;font-size: 30px;}\n</style>\n</head>\n" +
                 "<body style=\"background-color: #33332C; color: #CCCCCC; font-size: 20px;\">"+name+html+html2+html3+"</body>\n</html>"
         return result
     }
    @SuppressLint("SetJavaScriptEnabled")
    fun changeView(){
        setContentView(R.layout.view_animal_result)
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
        if(image_id!=-1L){
            val iv = findViewById<ImageView>(R.id.image_compare)
            val observation = controller.getObservationWithId(image_id)
            Glide
                    .with(this)
                    .asBitmap()
                    .load(Uri.fromFile(File(observation?.uris)))
                    .into(iv)
        }

        val wv = findViewById<WebView>(R.id.webView2)

        val image = findViewById<ImageView>(R.id.image)

        wv.settings.javaScriptEnabled = true
        //Log.d("html for wv",data)
        wv.loadDataWithBaseURL(null,prepareHtml(animal_result.name,animal_result.desc,animal_result.gallery,animal_result.sys),mime,encoding,null)
        Glide
                .with(this)
                .asBitmap()
                .load(animal_result.image_url)
                .into(image)


    }

    override fun onItemClick(index: Int) {
        link = "https://www.ekologia.pl/wiedza/zwierzeta/"
        if(index<result_list.size){
            link += normalize(result_list[index].name.substringBefore('[').toLowerCase()).replace(' ', '-')
            history.add(link)

            val fetcher = AnimalDataFetcher(false,link)
            fetcher.execute()
        }


    }

    private val set = HashSet<String>()

    fun add(word: String) {
        var word = word
        word = word.trim { it <= ' ' }.toLowerCase()
        word = normalize(word)
        set.add(word)
    }

    operator fun contains(string: String): Boolean {
        return set.contains(string) || set.contains(normalize(string))
    }

    private fun normalizeChar(c: Char): Char {
        when (c) {
            'ą' -> return 'a'
            'ć' -> return 'c'
            'ę' -> return 'e'
            'ł' -> return 'l'
            'ń' -> return 'n'
            'ó' -> return 'o'
            'ś' -> return 's'
            'ż', 'ź' -> return 'z'
        }
        return c
    }

    private fun normalize(word: String): String {
        if (word == null || "" == word) {
            return word
        }
        val charArray = word.toCharArray()
        val normalizedArray = CharArray(charArray.size)
        for (i in normalizedArray.indices) {
            normalizedArray[i] = normalizeChar(charArray[i])
        }
        return String(normalizedArray)
    }

    override fun onBackPressed() {
        if(history.size>1){
            history.removeAt(history.size - 1)
            link = history[history.size - 1]

            if(animal) {
                animal = false
                setContentView(R.layout.activity_animal_search)
                if(image_id!=-1L){
                    val iv = findViewById<ImageView>(R.id.image_compare)
                    val observation = controller.getObservationWithId(image_id)
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(Uri.fromFile(File(observation?.uris)))
                        .into(iv)
                }
                val rv = findViewById<RecyclerView>(R.id.rv)
                adapter = SearchResultAdapter(result_list, this)
                adapter.setOnClickListener(this)
                rv.adapter = adapter
                rv.layoutManager = LinearLayoutManager(this)
            }
            val fetcher = AnimalDataFetcher(false,link)
            fetcher.execute()


        }
        else{
            super.onBackPressed()
        }


    }
}