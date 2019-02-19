package com.example.alex.gymapp.services

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.alex.gymapp.model.Exercise
import com.example.alex.gymapp.model.ExercisesIteratorCustom
import com.example.alex.gymapp.utilities.ServiceNotification
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class ScheduleService : Service() {

    lateinit var realm: Realm
    lateinit var exerciseIteratorCustom: ExercisesIteratorCustom
    private var exerciseCounter: Int = 0

    private val myBinder = MyLocalBinder()

    // These are the Intent actions that we are prepared to handle. Notice that
    // the fact these
    // constants exist in our class is a mere convenience: what really defines
    // the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for
    // our service in
    // AndroidManifest.xml.
    val ACTION_START = "SCHEDULE_START"
    val ACTION_NEXT = "NEXT_EXERCISE"
    val ACTION_PREVIOUS = "PREVIOUS_EXERCISE"


    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : ScheduleService {
            return this@ScheduleService
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action

        if(action == ACTION_NEXT){
            nextExercise()
        }
        else if (action == ACTION_PREVIOUS){
            previousExercise()
        }
        else if (action == ACTION_START){

            val executionDay = intent.getIntExtra("currentExecutionDay",0)

            exerciseCounter = 0

            //Instance realm
            realm = Realm.getDefaultInstance()

            //Get exercises stack of the selected day
            getExercisesOfSchedule(executionDay)
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private fun getExercisesOfSchedule(executionDay:Int) {
        //Get exercises of the selected schedule
        val exercises = realm.where<Exercise>().equalTo("executionDay", executionDay).findAll().sort("position")

        //Create iterator
        exerciseIteratorCustom = ExercisesIteratorCustom(exercises.toList())
    }

    private fun nextExercise(){
        val exercise = exerciseIteratorCustom.next()

        if(exercise == null){
            stopForeground(true)
            return
        }

        ServiceNotification(applicationContext, this).updateNotification(exercise.name,"message")
    }

    private fun previousExercise(){

        val exercise = exerciseIteratorCustom.previous()

        if(exercise == null){
            return
        }

        ServiceNotification(applicationContext, this).updateNotification(exercise.name,"message")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}