package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.alex.gymapp.model.Exercise
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_weights.*

class ExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(toolbar)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        val exerciseId = intent.getIntExtra("exerciseId",0)

        realm = Realm.getDefaultInstance()

        val exercise = realm.where<Exercise>().equalTo("id",exerciseId).findFirst()

        toolbar.title = exercise!!.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
