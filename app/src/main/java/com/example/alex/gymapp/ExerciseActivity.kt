package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.fragment_weights.*

class ExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        //Get Exercise
        val exerciseId = intent.getLongExtra("exerciseId",0)
        realm = Realm.getDefaultInstance()
        exercise = realm.where<Exercise>().equalTo("id",exerciseId).findFirst()!!

        //Show exercise data
        titleTw.text = exercise.name
        weightTw.text = String.format("%1$,.2f Kg", exercise.weight)
        restTw.text =String.format("%dM %dS", exercise.minutesOfRest, exercise.secondsOfRest)
        seriesTw.text =String.format("%dx%d ", exercise.series,exercise.repetitions )
        executionDayTw.text = exercise.executionDay

        backBtn.setOnClickListener{
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
