package com.example.alex.gymapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.alex.gymapp.R
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_exercise.*

class EditExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)

        //Get Exercise
        val exerciseId = intent.getLongExtra("exerciseId", 0)
        realm = Realm.getDefaultInstance()
        exercise = realm.where<Exercise>().equalTo("id", exerciseId).findFirst()!!

        //Set exercise data
        nameET.setText(exercise.name)
        weightET.setText(exercise.weight.toString())
        minutesRestET.setText(exercise.minutesOfRest.toString())
        secondsRestET.setText(exercise.secondsOfRest.toString())
        seriesET.setText(exercise.series.toString())
        repetitionsET.setText(exercise.repetitions.toString())
        //executionDaySpinner2.text = exercise.executionDay

        cancelBtn.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
            //TODO add popup for confirmation and pending changes
        }

        saveBtn.setOnClickListener {
            Save()
            finish()
            overridePendingTransition(0, 0)
            //TODO Add Callback or intent with result dont know
        }
    }

    private fun Save(){

        //TODO Execution day

        //Get data
        var name = nameET.text.toString()
        var weightValue = java.lang.Double.parseDouble(weightET.text.toString())
        var minutesOfRestValue = java.lang.Integer.parseInt(minutesRestET.text.toString())
        var secondOfRestValue = java.lang.Integer.parseInt(secondsRestET.text.toString())
        var seriesValue = java.lang.Integer.parseInt(seriesET.text.toString())
        var repetitionsValue =  java.lang.Integer.parseInt(repetitionsET.text.toString())

        //Update on DB
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            //Update Exercise
            exercise.name = name
            exercise.weight = weightValue
            exercise.minutesOfRest = minutesOfRestValue
            exercise.secondsOfRest = secondOfRestValue
            exercise.series = seriesValue
            exercise.repetitions = repetitionsValue
            //exercise.executionDay = selectedExecutionDay
            realm.insertOrUpdate(exercise)
        }


    }
}
