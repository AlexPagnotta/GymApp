package com.example.alex.gymapp

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.alex.gymapp.services.ScheduleService
import com.example.alex.gymapp.utilities.GetScheduleServiceNotification
import kotlinx.android.synthetic.main.activity_schedule_start.*

class ScheduleStartActivity : AppCompatActivity() {

    var scheduleService: ScheduleService? = null
    var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as ScheduleService.MyLocalBinder
            scheduleService = binder.getService()
            isBound = true

            GetScheduleServiceNotification(applicationContext, scheduleService).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_start)

        val serviceIntent = Intent(applicationContext, ScheduleService::class.java)

        startScheduleBtn.setOnClickListener {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        stopScheduleBtn.setOnClickListener {
            //Unbind Service
            try {
                unbindService(serviceConnection)
            } catch (e: IllegalArgumentException) {
                Log.w("", "Error Unbinding Service.")
            }
            // If the service is not running then start it
            if (isServiceRunning(ScheduleService::class.java)) {
                // Stop the service
                stopService(serviceIntent)
            }
        }
    }

    override fun onDestroy() {
        val serviceClass = ScheduleService::class.java
        val serviceIntent = Intent(applicationContext, serviceClass)
        try {
            unbindService(serviceConnection)
        } catch (e: IllegalArgumentException) {
            Log.w("MainActivity", "Error Unbinding Service.")
        }

        if (isServiceRunning(ScheduleService::class.java)) {
            stopService(serviceIntent)
        }
        super.onDestroy()
    }

    // Custom method to determine whether a service is running
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

}
