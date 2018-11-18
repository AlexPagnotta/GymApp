package com.example.alex.gymapp.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import com.example.alex.gymapp.R
import com.example.alex.gymapp.services.ScheduleService

class GetScheduleServiceNotification(
        private val context: Context, private var myService: ScheduleService?) : AsyncTask<Long, Void, Any>() {

    private lateinit var mNotification: Notification
    
    companion object {
        const val CHANNEL_ID = "1"
        const val CHANNEL_NAME = "Sample Notification"
    }

    override fun doInBackground(vararg params: Long?): Any? {
        //Create Channel
        createChannel(context)
        val notifyIntent = Intent(context, ScheduleService::class.java)
        val title = "Sample Notification"
        val message = "You have received a sample notification. This notification will take you to the details page."
        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("message", message)
        notifyIntent.putExtra("notification", true)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotification = Notification.Builder(context, CHANNEL_ID)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle()
                            .bigText(message))
                    .setContentText(message).build()
        } else {
            mNotification = Notification.Builder(context)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(Notification.BigTextStyle()
                            .bigText(message))
                    .setContentText(message).build()
        }
        myService?.startForeground(1, mNotification)
        return null
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(false)
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = "notification channel description"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}