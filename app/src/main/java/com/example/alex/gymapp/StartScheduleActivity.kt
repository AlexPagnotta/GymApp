package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import java.util.*
import kotlin.concurrent.schedule

class StartScheduleActivity : AppCompatActivity() {

    lateinit var realm: Realm
    private var executionDay: Int = 0
    lateinit var exercises: RealmResults<Exercise>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_start)

        //Get executionDay
        executionDay = intent.getIntExtra("currentExecutionDay",0)

        //Instance realm
        realm = Realm.getDefaultInstance()

        //Get exercises
        exercises = getExercisesOfSchedule()

        //Show the startScheduleFragments
        showStartScheduleFragment()
    }

    private fun getExercisesOfSchedule() : RealmResults<Exercise> {
        //Get exercises of the selected schedule
        return realm.where<Exercise>().equalTo("executionDay", executionDay).findAll().sort("position")
    }

    private fun showStartScheduleFragment(){
        //Start fragment
        val startScheduleFragment = StartScheduleFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()

        //Pass the selected day to fragment
        val arguments = Bundle()
        arguments.putInt("currentExecutionDay", executionDay)
        startScheduleFragment.arguments = arguments

        //Show fragment
        transaction.replace(R.id.fragmentContainer, startScheduleFragment)
        transaction.commit()
    }

    fun startSchedule(){
        //Put exercises in a stack
        var exercisesStack = Stack<Exercise>()
        for (exercise in exercises){
            //Add copied exercise to stack, copy beacuse cannot use realm object on different threads
            exercisesStack.push( realm.copyFromRealm(exercise))
        }

        //TODO Load exercise every X Seconds

    }

    fun loadNextExercise(exercise:Exercise){
        //Start fragment
        val runningExerciseFragment = RunningExerciseFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()

        //Pass the current exercise id to fragment
        val arguments = Bundle()
        arguments.putLong("exerciseId", exercise.id)
        runningExerciseFragment.arguments = arguments

        transaction.replace(R.id.fragmentContainer, runningExerciseFragment)
        transaction.commit()
    }
}
