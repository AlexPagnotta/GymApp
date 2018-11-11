package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

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
        //Start fragment
        val runningExerciseFragment = RunningExerciseFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, runningExerciseFragment)
        transaction.commit()
    }
}
