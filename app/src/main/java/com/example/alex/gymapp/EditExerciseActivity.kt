package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_exercise.*
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import io.realm.kotlin.createObject


class EditExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise
    lateinit var selectedExecutionDay : String

    var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)

        val weekDays = resources.getStringArray(R.array.days_of_week)
        selectedExecutionDay = weekDays[0]

        if(intent.hasExtra("exerciseId")){
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
            selectedExecutionDay = exercise.executionDay

            isEditMode = true
        }

        setupExecutionDaySpinner()

        cancelBtn.setOnClickListener {
            //TODO pending changes check

            val builder = AlertDialog.Builder(this)
            //builder.setTitle("");
            builder.setMessage("Are you sure you want to discard these changes?")
                    .setCancelable(false)
                    .setPositiveButton("CONFIRM"
                    ) { _, _ ->
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_CANCELED, returnIntent)
                        finish()
                        overridePendingTransition(0, 0)
                    }
                    .setNegativeButton("CANCEL") { _, _ ->}

            builder.show()
        }

        saveBtn.setOnClickListener {
            Save()
            val returnIntent = Intent()
            returnIntent.putExtra("executionDay",exercise.executionDay)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun setupExecutionDaySpinner(){
        val weekDays = resources.getStringArray(R.array.days_of_week)
        val position = weekDays.indexOf(selectedExecutionDay)

        val dataAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                weekDays)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        executionDaySpinner.adapter = dataAdapter

        executionDaySpinner.setSelection(position)

        executionDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                selectedExecutionDay = parentView.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun Save(){
        //Get data
        var name = nameET.text.toString()
        var weightValue = java.lang.Double.parseDouble(weightET.text.toString())
        var minutesOfRestValue = java.lang.Integer.parseInt(minutesRestET.text.toString())
        var secondOfRestValue = java.lang.Integer.parseInt(secondsRestET.text.toString())
        var seriesValue = java.lang.Integer.parseInt(seriesET.text.toString())
        var repetitionsValue =  java.lang.Integer.parseInt(repetitionsET.text.toString())

        val realm = Realm.getDefaultInstance()

        if(isEditMode){
            //Update on DB
            realm.executeTransaction { realm ->
                //Update Exercise
                exercise.name = name
                exercise.weight = weightValue
                exercise.minutesOfRest = minutesOfRestValue
                exercise.secondsOfRest = secondOfRestValue
                exercise.series = seriesValue
                exercise.repetitions = repetitionsValue
                exercise.executionDay = selectedExecutionDay

                realm.insertOrUpdate(exercise)
            }
        }
        else{
            realm.executeTransaction { realm ->
                val currentIdNum = realm.where<Exercise>().max("id")
                val nextId: Int
                if (currentIdNum == null) {
                    nextId = 1
                } else {
                    nextId = currentIdNum!!.toInt() + 1
                }

                var newExercise = realm.createObject<Exercise>(nextId)
                newExercise.name = name
                newExercise.weight = weightValue
                newExercise.minutesOfRest = minutesOfRestValue
                newExercise.secondsOfRest = secondOfRestValue
                newExercise.series = seriesValue
                newExercise.repetitions = repetitionsValue
                newExercise.executionDay = selectedExecutionDay

                exercise = newExercise
            }
        }
    }
}
