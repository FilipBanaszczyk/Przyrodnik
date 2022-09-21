package com.example.przyrodnik

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import java.util.*


class MyNotificationPublisher : BroadcastReceiver() {
    val channel_id = "primary_notification_channel"
    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = intent.getParcelableExtra(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        Log.d("Receiver","received $notificationId  "+ Date().toString())
        val notification_content = intent.getStringExtra(CONTENT)
        notificationManager.notify(notificationId, notification)
        val intent = Intent(context, MainMenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        var builder = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Zaplanowana obserwacja!")
            .setContentText(notification_content)
            .setLargeIcon(
                    (context.resources.getDrawable(R.drawable.app_icon) as BitmapDrawable).bitmap
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            Log.d("Notif","notif")
            notify(notificationId, builder.build())
        }


    }




    companion object {
        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION = "notification"
        var CONTENT = "content"
    }
}