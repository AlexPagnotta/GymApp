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
import com.example.alex.gymapp.extensions.onChange
import io.realm.kotlin.createObject

class EditExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise
    var selectedExecutionDay = 0

    //Bool to indicate if it's a new exercise on an existing one
    var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)

        //If the id is present is an existing note
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
        setEditTextsMinMax()

        cancelBtn.setOnClickListener {
            //TODO pending changes check
            val builder = AlertDialog.Builder(this)
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
            saveExercise()
            val returnIntent = Intent()
            returnIntent.putExtra("executionDay",exercise.executionDay)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun setupExecutionDaySpinner(){
        //Set spinner items with strings
        val weekDays = resources.getStringArray(R.array.days_of_week)
        val dataAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                weekDays)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        executionDaySpinner.adapter = dataAdapter
        //Set spinner selected item
        executionDaySpinner.setSelection(selectedExecutionDay)

        executionDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                selectedExecutionDay = position
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    //Set minimum and maximum value of edit texts
    private fun setEditTextsMinMax(){
        //Weight ET
        weightET.onChange {
            if(!it.isEmpty()) {
                val number = java.lang.Double.parseDouble(it)
                if (number > 1000) {
                    weightET.text!!.replace(0, it.length, "999", 0, 3)
                }
            }
        }

        //MinutesRest ET
        minutesRestET.onChange {
            if(!it.isEmpty()){
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    minutesRestET.text!!.replace(0, it.length, "60", 0, 2)
                }
            }
        }

        //SecondsRest ET
        secondsRestET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    secondsRestET.text!!.replace(0, it.length, "59", 0, 2)
                }
            }
        }

        //series ET
        seriesET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    seriesET.text!!.replace(0, it.length, "99", 0, 2)
                }
            }
        }

        //repetitions ET
        repetitionsET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    repetitionsET.text!!.replace(0, it.length, "99", 0, 2)
                }
            }
        }
    }

    private fun saveExercise(){

        //Get data
        val name = nameET.text.toString()

        var weightValue = 0.0
        try{
            weightValue = java.lang.Double.parseDouble(weightET.text.toString())
        }
        catch (e: Exception){ }

        var minutesOfRestValue = 0
        try{
            minutesOfRestValue = java.lang.Integer.parseInt(minutesRestET.text.toString())
        }
        catch (e: Exception){ }

        var secondOfRestValue = 0
        try{
            secondOfRestValue = java.lang.Integer.parseInt(secondsRestET.text.toString())
        }
        catch (e: Exception){ }

        var seriesValue = 0
        try{
            seriesValue = java.lang.Integer.parseInt(seriesET.text.toString())
        }
        catch (e: Exception){ }

        var repetitionsValue = 0
        try{
            repetitionsValue = java.lang.Integer.parseInt(repetitionsET.text.toString())
        }
        catch (e: Exception){ }

        val realm = Realm.getDefaultInstance()

        //Update the edited exercise
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
        //Save the new exercise
        else{
            realm.executeTransaction { realm ->
                val currentIdNum = realm.where<Exercise>().max("id")
                val nextId: Int
                if (currentIdNum == null) {
                    nextId = 1
                } else {
                    nextId = currentIdNum.toInt() + 1
                }

                val newExercise = realm.createObject<Exercise>(nextId)
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
