package com.example.alex.gymapp.services

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class ScheduleService : Service() {

    lateinit var realm: Realm
    lateinit var exercisesStack: Stack<Exercise>

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
        else if (action == ACTION_START){
            val executionDay = intent.getIntExtra("currentExecutionDay",0)

            //Instance realm
            realm = Realm.getDefaultInstance()

            //Get exercises stack of the selected day
            exercisesStack = getExercisesOfSchedule(executionDay)
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    private fun getExercisesOfSchedule(executionDay:Int) : Stack<Exercise> {
        //Get exercises of the selected schedule
        val exercises = realm.where<Exercise>().equalTo("executionDay", executionDay).findAll().sort("position")

        //Create stack
        val exercisesStack = Stack<Exercise>()
        for (exercise in exercises){
            //Add copied exercise to stack, copy because cannot use realm object on different threads
            exercisesStack.push(realm.copyFromRealm(exercise))
        }

        return exercisesStack
    }

    private fun nextExercise(){
        if(exercisesStack.count() == 0){
            stopForeground(true)
            return
        }

        var exercise = exercisesStack.pop()
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}