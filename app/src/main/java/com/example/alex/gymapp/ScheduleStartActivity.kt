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
import com.example.alex.gymapp.model.Exercise
import com.example.alex.gymapp.services.ScheduleService
import com.example.alex.gymapp.utilities.ServiceNotification
import kotlinx.android.synthetic.main.activity_schedule_start.*

class ScheduleStartActivity : AppCompatActivity() {

    val ACTION_START = "SCHEDULE_START"
    val ACTION_NEXT = "NEXT_EXERCISE"
    val ACTION_PREVIOUS = "PREVIOUS_EXERCISE"

    var scheduleService: ScheduleService? = null
    var isBoundToService = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as ScheduleService.MyLocalBinder
            scheduleService = binder.getService()
            isBoundToService = true

            ServiceNotification(applicationContext, scheduleService).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBoundToService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_start)

        //Get executionDay
        val executionDay = intent.getIntExtra("currentExecutionDay",0)

        //Setup service intent and pass executionDay
        val serviceIntent = Intent(applicationContext, ScheduleService::class.java)
        serviceIntent.putExtra("currentExecutionDay",  executionDay)

        serviceIntent.action = ACTION_START
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        if(scheduleService!= null){
            LoadExerciseFragment(scheduleService!!.exerciseIteratorCustom.current())
        }

        stopServiceBtn.setOnClickListener {
            //Unbind Service
            try {
                unbindService(serviceConnection)
            } catch (e: IllegalArgumentException) {
                Log.w("", "Error Unbinding Service.")
            }
            // If the service is running stop it
            if (isServiceRunning(ScheduleService::class.java)) {
                // Stop the service
                stopService(serviceIntent)
            }
        }

        previousExerciseBtn.setOnClickListener {
            serviceIntent.action = ACTION_PREVIOUS
            startService(serviceIntent)

            if(scheduleService!= null){
                LoadExerciseFragment(scheduleService!!.exerciseIteratorCustom.current())
            }
        }

        nextExerciseBtn.setOnClickListener {
            serviceIntent.action = ACTION_NEXT
            startService(serviceIntent)

            if(scheduleService!= null){
                LoadExerciseFragment(scheduleService!!.exerciseIteratorCustom.current())
            }
        }


    }

    override fun onDestroy() {
        val serviceClass = ScheduleService::class.java
        val serviceIntent = Intent(applicationContext, serviceClass)
        try {
            unbindService(serviceConnection)
        } catch (e: IllegalArgumentException) {
            Log.w("Schedule Start Activity", "Error Unbinding Service.")
        }
        // If the service is running stop it
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

    private fun LoadExerciseFragment(exercise: Exercise){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val exerciseFragment = ServiceExerciseFragment.newInstance(exercise.id)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.serviceExerciseFrameLayout, exerciseFragment)
                .commit()
    }
}
