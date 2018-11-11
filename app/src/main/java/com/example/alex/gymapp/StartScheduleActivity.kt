package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class StartScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_start)
        scheduleStart()
    }

    private fun scheduleStart(){
        val startScheduleFragment = StartScheduleFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, startScheduleFragment)
        transaction.commit()
    }
}
