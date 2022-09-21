package com.example.przyrodnik

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ApplicationController: Application() {

    lateinit var realm: Realm
    lateinit var alarm_manager : AlarmManager
    var day: String = ""
    var type: String = ""
    var from_animals = true
    val db = DataBaseManager()
    val files = FileManager()
    val location =  LocationProvider()
    val notif = NotificationManager()
    val selected_obs = ArrayList<Long>()


    private val REQUEST_CHECK_SETTINGS = 100
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(config)
        alarm_manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    }
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    fun goToMain(){
        val intent = Intent(this, MainMenuActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }
    fun goToMap(){
        val intent = Intent(this, MapActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }
    fun goToNote(){
        val intent = Intent(this, NoteActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    fun goToJournal(){

        val intent = Intent(this, JournalActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)

    }
    fun goToPlaning(){
        val intent = Intent(this, PlaningActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)

    }
    fun goToStats(){
        val intent = Intent(this, SummaryActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)

    }
    fun goToAnimalSearch(id: Long, image: Boolean){
        if(image){
            val intent = Intent(this, AnimalSearchActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("compare", id)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, AnimalSearchActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("compare", -1L)
            startActivity(intent)
        }



    }
    fun backFromObservations(){

        val intent = Intent(this, JournalActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)


    }
    fun goToObservationView(obs: Observation){
        val intent = Intent(this, ObservationViewActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("observation id", obs.id)
        startActivity(intent)

    }
    fun goToJournalDay(day: String){
        from_animals = false
        if(day!=""){
            this.day = day
        }

        val intent = Intent(this, JournalItemActivity::class.java)
        intent.putExtra("factor", "day")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    fun goToAnimalType(type: String){
        from_animals = true
        if(type!=""){
            this.type = type
        }

        val intent = Intent(this, JournalItemActivity::class.java)
        intent.putExtra("factor", "type")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showObservationOnMap(obs: Observation){
        val intent = Intent(this, ObservationMapActivity::class.java)
        intent.putExtra("observation", obs.id)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
    fun showPlanMap(plan: PlanItem){
        val intent = Intent(this, PlanMapActivity::class.java)
        intent.putExtra("plan", plan)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    fun putToDB(uri: String, activity: AppCompatActivity){

        val obs = Observation()
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var ll: MyLocationListener? = MyLocationListener()
        var location:Location? = null
        var latitude = 0.0
        var longitude = 0.0

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, ll!!)
            Log.d("GPS Enabled", "GPS Enabled")
            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location.getLatitude()
                    longitude = location.getLongitude()
                }
            }
            lm.removeUpdates(ll)
            ll = null


            Log.d("coords:", "${latitude}    ${longitude}")

        }



        realm.beginTransaction()
        var last_id = realm.where(Observation::class.java).max("id") as Long?
        if(last_id==null){
            last_id = 1
        }
        else{
            last_id=last_id+1
        }

        obs.id = last_id
        Log.d("Save image", uri)
        obs.uris = uri
        obs.date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        obs.animal_type="Nieznane"
        obs.lat = latitude
        obs.lng = longitude
        realm.copyToRealm(obs)
        realm.commitTransaction()
        Log.d("adding image to db", "id: ${last_id}")


    }
    fun getObservations(): ArrayList<Observation>{
        val result = ArrayList<Observation>()
        val obs = realm.where(Observation::class.java).findAll()
        for(ob in obs){
            result.add(ob)
        }
        return  result
    }

    fun getPlans(): ArrayList<PlanItem>{
        val result = ArrayList<PlanItem>()
        val plans = realm.where(PlanItem::class.java).findAll()
        for(plan in plans){
            result.add(plan)
        }
        return  result
    }

    fun updateObservation(observation: Observation, type: String){
        realm.executeTransaction {
            observation?.animal_type = type
        }
        realm.beginTransaction()
        realm.copyToRealm(observation)
        realm.commitTransaction()

    }
    fun getObservationWithId(id: Long?): Observation?{
        return realm.where((Observation::class.java)).equalTo("id", id).findFirst()
    }
    fun countDateObservations(): ArrayList<String>{
        val result = ArrayList<String>()
        val distinct_dates = realm.where(Observation::class.java).findAll()
        for(i in 0 until distinct_dates.size){
            val date = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd_HHmm").parse(distinct_dates[i]?.date))
            if(!result.contains(date)){
                //Log.d("new dates counted", i.toString())
                result.add(date)
            }
            else{
                //Log.d("dates counted ", i.toString())

            }
        }
        return result
    }
    fun getEarliestDate():Date{
        val obs = realm.where(Observation::class.java).findAll()
        var min = Date()
        for(i in 0 until obs.size) {
            val date = SimpleDateFormat("yyyyMMdd_HHmm").parse(obs[i]?.date)
            if(date<min){
                min = date
            }

        }
        return min
        
    }
    fun getObservationsFromRange(start: Date, end: Date): ArrayList<StatisticItem>{
        val result = ArrayList<StatisticItem>()
        val chosen = ArrayList<Observation>()
        val obs = realm.where(Observation::class.java).findAll()

        for(i in 0 until obs.size) {
            val date = SimpleDateFormat("yyyyMMdd_HHmm").parse(obs[i]?.date)
            Log.d("dates compare", "$start   $end   $date")
            if(date<=end && date>=start){
                Log.d("date between", " ")
                chosen.add(obs[i]!!)
            }
        }
        val types = ArrayList<String>()
        for (i in 0 until chosen.size){
            val date = chosen[i].animal_type
            if(!types.contains(date)){
                Log.d("group by type new type", date)
                types.add(date)
            }
        }

        var total_p = 0
        var total_v = 0
        var total_a = 0
        var total_n = 0

        for(i in 0 until types.size){

            var photos = 0
            var videos = 0
            var audio = 0
            var notes = 0
            for(j in 0 until chosen.size){
                if(chosen[j].animal_type == types[i])
                when (getFormat(chosen[j].uris)){
                    "jpg"-> photos++
                    "mp4" -> videos++
                    "3gp" -> audio++
                    "txt" -> notes++
                }
            }
            val sum = photos + videos + audio + notes
            total_p += photos
            total_v += videos
            total_a += audio
            total_n += notes
            Log.d("item added to stats", "$photos   $videos  $audio   $notes   $sum")
            result.add(StatisticItem(types[i],photos, videos, audio, notes, sum))

        }
        if(result.size!=0){
            result.add(StatisticItem("Suma", total_p, total_v, total_a, total_n,(total_a+total_n+total_p+total_v)))
            Log.d("item added to stats", "$total_p   $total_v  $total_a   $total_n")
        }
        return result
    }
    fun getFormat(uri: String):String{
        //Log.d("getformat", uri.subSequence(uri.length-3,uri.length).toString())
        return uri.subSequence(uri.length-3,uri.length).toString()
    }
    fun groupByDate(list: ArrayList<Observation>):LinkedHashMap<String, ArrayList<Observation>>{
        val result = LinkedHashMap<String, ArrayList<Observation>>()
        val dates = ArrayList<String>()
        for (i in 0 until list.size){
            val date = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd_HHmm").parse(list[i].date))
            if(!dates.contains(date)){
                //Log.d("group by date new date", date)
                dates.add(date)
            }
        }
        for(i in 0 until dates.size){
            val obs = ArrayList<Observation>()
            for(j in 0 until list.size){
                val date = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyyMMdd_HHmm").parse(list[j].date))
                if(date==dates[i]){
                    //Log.d("group by date new obs", "${list[j]}  $dates[i]}")
                    obs.add(list[j])
                }
            }
            result.put(dates[i], obs)
        }
       // Log.d("group by date", result.size.toString())
        return  result
    }
    fun groupByType(list: ArrayList<Observation>):LinkedHashMap<String, ArrayList<Observation>>{
        val result = LinkedHashMap<String, ArrayList<Observation>>()
        val types = ArrayList<String>()
        for (i in 0 until list.size){
            val date = list[i].animal_type
            if(!types.contains(date)){
                //Log.d("group by type new type", date)
                types.add(date)
            }
        }
        for(i in 0 until types.size){
            val obs = ArrayList<Observation>()
            for(j in 0 until list.size){
                if(list[j].animal_type==types[i]){
                    //Log.d("group by type new obs", "${list[j]}  ${types[i]}")
                    obs.add(list[j])
                }
            }
            result.put(types[i], obs)
        }
        //Log.d("group by type", result.size.toString())
        return  result
    }
    fun countObservationsByDateAndFormat(date: String, format: String):Long{
        val result = realm.where((Observation::class.java)).contains("uris", format).contains("date", date).count()
        //Log.d("controller $date format", result.toString())
        return  result
    }
    fun countObservationsByTypeAndFormat(type: String, format: String):Long{

        return  realm.where((Observation::class.java)).contains("uris", format).equalTo("animal_type", type).count()
    }
    fun getObservationWithTypeAndFormat(type: String?, format: String): ArrayList<Observation>{

        val result = ArrayList<Observation>()
        val obs = realm.where((Observation::class.java)).equalTo("animal_type", type).findAll()!!
       // Log.d("obs date and type", "found ${obs.size} from type $type")
        for (ob in obs){
           // Log.d( "obs date and type","${(ob.uris.contains(format)).toString()}   ${format}  ${ob.uris}" )
            if(ob.uris.contains(format)){

                result.add(ob)
            }
        }
        return result

    }

    fun getObservationWithDateAndFormat(date: String?, type: String): ArrayList<Observation>{
        val result = ArrayList<Observation>()
        val obs = realm.where((Observation::class.java)).contains("date", date).findAll()!!
       // Log.d("obs date and type", "found ${obs.size} from day $date")
        for (ob in obs){
           // Log.d( "obs date and type", "${(ob.uris.contains(type)).toString()}   ${type}  ${ob.uris}" )
            if(ob.uris.contains(type)){

                result.add(ob)
            }
        }
        return result
    }
    fun getNextObservation(): Observation{
        return Observation()
    }
    fun deleteSelected(){
        for(i in 0 until selected_obs.size){
            Log.d("delete selected", "${selected_obs[i]}")
            deleteObservation(selected_obs[i])
        }
        selected_obs.clear()
    }
    fun changeTypeSelected(type: String){
        for(i in 0 until selected_obs.size){
            Log.d("change type selected", "${selected_obs[i]}   $type")
            val obs =  realm.where((Observation::class.java)).equalTo("id", selected_obs[i]).findFirst()
            updateObservation(obs!!, type)
        }
        selected_obs.clear()
    }
    fun deleteObservation(id: Long){
        val result  = realm.where((Observation::class.java)).equalTo("id", id).findFirst()
        val uri = result?.uris
        realm.beginTransaction()
        result?.deleteFromRealm()
        realm.commitTransaction()
        val file = File(uri)
        file.delete()
    }

    fun getNotifications():ArrayList<PlanItem>{
        val result = ArrayList<PlanItem>()
        val plan = realm.where((PlanItem::class.java)).findAll()
        for (item in plan){
            if(getDelay(item.time)<=0){
                realm.executeTransaction {
                    item.state = false
                }

            }
            result.add(item!!)
        }
        return result
    }
    fun changeNotificationDescription(desc: String, id: Long){
        val new_plan = realm.where(PlanItem::class.java).equalTo("id", id).findFirst()!!
        realm.executeTransaction {
            new_plan.desc = desc


        }
        realm.beginTransaction()
        realm.copyToRealm(new_plan)
        realm.commitTransaction()

    }


    fun changePlanLocalization(plan: PlanItem, latLng: LatLng){
        val new_plan = realm.where(PlanItem::class.java).equalTo("id", plan.id).findFirst()!!
        realm.executeTransaction {
            new_plan.lat = latLng.latitude
            new_plan.lng = latLng.longitude

        }
        realm.beginTransaction()
        realm.copyToRealm(new_plan)
        realm.commitTransaction()
        Toast.makeText(this, "Zapisano lokalizację planowanej obserwacji", Toast.LENGTH_SHORT)

    }
    fun newNotification(name: String, new_date: Calendar):PlanItem{
        val result = PlanItem()


        realm.beginTransaction()
        var last_id = realm.where(PlanItem::class.java).max("id") as Long?
        if(last_id==null){
            last_id = 1
        }
        else{
            last_id=last_id+1
        }
        result.id = last_id
        result.time = new_date.time
        result.content = name
        result.state = false


        realm.copyToRealm(result)
        realm.commitTransaction()
        return result

    }
    private fun getDelay(time: Date):Long{
        val now = Date()
        val diff = time.time - now.time
        if(diff>0 )Log.d("diff", diff.toString())
        return diff
    }
    fun schedule(id: Long):PlanItem?{
        val plan = realm.where((PlanItem::class.java)).equalTo("id", id).findFirst()
        if(plan!=null && getDelay(plan.time)>0){
            scheduleNotification(this, plan)
            realm.executeTransaction {
                plan.state = true
            }
            realm.beginTransaction()
            realm.copyToRealm(plan)
            realm.commitTransaction()
            Log.d("plan state schedule", plan.state.toString())

        }
        return plan

    }
    fun scheduleNotification(context: Context, plan: PlanItem) {
        val delay = getDelay(plan.time)
        Log.d("scheduleNot", plan.id.toString())
        val builder = NotificationCompat.Builder(context)
                .setContentTitle("Zaplanowana obserwacja!")
                .setContentText(plan.content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(
                        (context.resources.getDrawable(R.drawable.app_icon) as BitmapDrawable).bitmap
                )
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        val intent = Intent(context, MainMenuActivity::class.java)
        val activity = PendingIntent.getActivity(
                context,
                plan.id.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
        builder.setContentIntent(activity)
        val notification: Notification = builder.build()
        val notificationIntent = Intent(context, MyNotificationPublisher::class.java)
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, plan.id.toInt())
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
        notificationIntent.putExtra(
                MyNotificationPublisher.CONTENT,
                plan.content
        )
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                plan.id.toInt(),
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
        if(delay>0){
            val futureInMillis = SystemClock.elapsedRealtime() + delay
            Log.d("schedule", futureInMillis.toString())
            alarm_manager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
        }

    }
    fun deletePlanItem(id: Long){
        realm.beginTransaction()
        realm.where(PlanItem::class.java).equalTo("id", id).findFirst()?.deleteFromRealm()
        realm.commitTransaction()

    }
    fun cancel(id: Long):PlanItem?{

        val plan = realm.where((PlanItem::class.java)).equalTo("id", id).findFirst()
        if(plan!=null){
            Log.d("controller", " cancel notif")
            cancelNotification(this, plan)
            realm.executeTransaction {
                plan.state = false
            }
            realm.beginTransaction()
            realm.copyToRealm(plan)
            realm.commitTransaction()
        }
        return plan
    }
    fun changeNotificationContent(id: Long, content: String){
        val plan = realm.where((PlanItem::class.java)).equalTo("id", id).findFirst()
        if(plan!=null){
            realm.executeTransaction {
                Log.d("change notif name","${plan.content}  $content")
                plan.content = content
            }
            realm.beginTransaction()
            realm.copyToRealm(plan)
            realm.commitTransaction()
            scheduleNotification(this, plan)

        }

    }
    fun changeNotificationDate(id: Long, date: Date){
        val plan = realm.where((PlanItem::class.java)).equalTo("id", id).findFirst()
        if(plan!=null){
            realm.executeTransaction {
                plan.time = date
            }
            realm.beginTransaction()
            realm.copyToRealm(plan)
            realm.commitTransaction()
            scheduleNotification(this, plan)

        }

    }
    fun cancelNotification(context: Context, plan: PlanItem){
        Log.d("cancelNot", plan.id.toString())
        val builder = NotificationCompat.Builder(context)
                .setContentTitle("Zaplanowana obserwacja!")
                .setContentText(plan.content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(
                        (context.resources.getDrawable(R.drawable.app_icon) as BitmapDrawable).bitmap
                )
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        val intent = Intent(context, MainMenuActivity::class.java)
        val activity = PendingIntent.getActivity(
                context,
                plan.id.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
        builder.setContentIntent(activity)
        val notification: Notification = builder.build()
        val notificationIntent = Intent(context, MyNotificationPublisher::class.java)
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, plan.id.toInt())
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                plan.id.toInt(),
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarm_manager.cancel(pendingIntent)
    }
}