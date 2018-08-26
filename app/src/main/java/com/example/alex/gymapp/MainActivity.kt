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

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar

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

        //Start first fragment
        val meFragment = MeFragment.newInstance()
        openFragment(meFragment)

        val realm = Realm.getDefaultInstance()

        //TEST
        realm.executeTransaction { realm ->
            // Add a person
            val currentIdNum = realm.where<Weight>().max("id")
            val nextId: Int
            if (currentIdNum == null) {
                nextId = 1
            } else {
                nextId = currentIdNum!!.toInt() + 1
            }
            val weight = realm.createObject<Weight>(nextId)
            weight.weight = 123.0
        }


    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
