package com.example.alex.gymapp.services

import android.app.Notification
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.example.alex.gymapp.R.mipmap.ic_launcher
import com.example.alex.gymapp.MainActivity
import com.example.alex.gymapp.R
import com.example.alex.gymapp.StartScheduleActivity


class ScheduleService : Service() {

    private val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
    val  ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
    val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action

        when (action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startScheduleService()
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopScheduleService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startScheduleService(){

        val notificationIntent = Intent(this, StartScheduleActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build()

        startForeground(1337, notification)
    }

    private fun stopScheduleService() {
        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }
}