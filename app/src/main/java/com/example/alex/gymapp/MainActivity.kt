package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import android.R.attr.fragment
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar

    var startScheduleOnCurrentDay = false

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_me -> {
                val meFragment = MeFragment.newInstance()
                openFragment(meFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_schedule -> {
                val scheduleFragment = ScheduleFragment.newInstance()
                openFragment(scheduleFragment)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weights -> {
                val weightsFragment = WeightsFragment.newInstance()
                openFragment(weightsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                val settingsFragments = SettingsFragments.newInstance()
                openFragment(settingsFragments)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Start Realm
        Realm.init(this)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Setting the very 1st item as home screen.
        bottomNavigation.selectedItemId = R.id.navigation_me

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)

        //Start the schedule of today
        if(fragment is ScheduleFragment && startScheduleOnCurrentDay){
            val arguments = Bundle()
            val c = Calendar.getInstance()
            val today = c.get(Calendar.DAY_OF_WEEK) - 1 //+1 to adjust indexes
            arguments.putInt("currentDay", today)
            fragment.setArguments(arguments)
            startScheduleOnCurrentDay = false
        }

        transaction.commit()
    }
}
