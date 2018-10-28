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

    var hasPendingChanges = false

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

            //Set exercise data and add tag to avoid setting pending changes
            nameET.tag = ""
            nameET.setText(exercise.name)
            nameET.tag = null

            weightET.tag = ""
            weightET.setText(exercise.weight.toString())
            weightET.tag = null

            minutesRestET.tag = ""
            minutesRestET.setText(exercise.minutesOfRest.toString())
            minutesRestET.tag = null

            secondsRestET.tag = ""
            secondsRestET.setText(exercise.secondsOfRest.toString())
            secondsRestET.tag = null

            seriesET.tag = ""
            seriesET.setText(exercise.series.toString())
            seriesET.tag = null

            repetitionsET.tag = ""
            repetitionsET.setText(exercise.repetitions.toString())
            repetitionsET.tag = null

            selectedExecutionDay = exercise.executionDay

            isEditMode = true
        }
        //Set the schedule day as the current one on the schedule fragment
        else if(intent.hasExtra("currentExecutionDay")) {
            val executionDay = intent.getIntExtra("currentExecutionDay", 0)
            selectedExecutionDay = executionDay
        }

        setupExecutionDaySpinner()
        setupEditTexts()

        cancelBtn.setOnClickListener {
            showConfirmDialog()
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

    private fun showConfirmDialog(){
        if(!hasPendingChanges){
            cancelChanges()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to discard these changes?")
                .setCancelable(false)
                .setPositiveButton("CONFIRM"
                ) { _, _ ->
                    cancelChanges()
                }
                .setNegativeButton("CANCEL") { _, _ ->}
        builder.show()
    }

    private fun cancelChanges(){
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun setupExecutionDaySpinner(){
        //Set spinner items with strings
        val weekDays = resources.getStringArray(R.array.days_of_week)
        val dataAdapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_toolbar_item,
                weekDays)
        dataAdapter.setDropDownViewResource(R.layout.spinner_toolbar_item_dropdown)
        executionDaySpinner.adapter = dataAdapter

        //Set spinner selected item
        //Add a tag to avoid set pending changes
        executionDaySpinner.tag = ""
        executionDaySpinner.setSelection(selectedExecutionDay)

        executionDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {

                if(selectedItemView == null){
                    return
                }

                selectedExecutionDay = position

                //Set pending changes only if set by user not programmatically
                if(executionDaySpinner.tag == null){
                    hasPendingChanges = true
                }
                else{
                    executionDaySpinner.tag = null
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    //Set minimum and maximum value of edit texts
    private fun setupEditTexts(){
        //NameEt
        nameET.onChange {
            if (nameET.tag == null) hasPendingChanges = true
        }

        //Weight ET
        weightET.onChange {
            if(!it.isEmpty()) {
                val number = java.lang.Double.parseDouble(it)
                if (number > 1000) {
                    weightET.text!!.replace(0, it.length, "999", 0, 3)
                }
            }
            if (weightET.tag == null) hasPendingChanges = true
        }

        //MinutesRest ET
        minutesRestET.onChange {
            if(!it.isEmpty()){
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    minutesRestET.text!!.replace(0, it.length, "60", 0, 2)
                }
            }
            if (minutesRestET.tag == null) hasPendingChanges = true
        }

        //SecondsRest ET
        secondsRestET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    secondsRestET.text!!.replace(0, it.length, "59", 0, 2)
                }
            }
            if (secondsRestET.tag == null) hasPendingChanges = true
        }

        //series ET
        seriesET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    seriesET.text!!.replace(0, it.length, "99", 0, 2)
                }
                else if(number < 1){
                    seriesET.text!!.replace(0, it.length, "1", 0, 1)
                }
            }
            if (seriesET.tag == null) hasPendingChanges = true
        }

        //repetitions ET
        repetitionsET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    repetitionsET.text!!.replace(0, it.length, "99", 0, 2)
                }
                else if(number < 1){
                    repetitionsET.text!!.replace(0, it.length, "1", 0, 1)
                }
            }
            if (repetitionsET.tag == null)  hasPendingChanges = true
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

        var seriesValue = 1
        try{
            seriesValue = java.lang.Integer.parseInt(seriesET.text.toString())
        }
        catch (e: Exception){ }

        var repetitionsValue = 1
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
                val lastId = realm.where<Exercise>().max("id")
                val nextId: Int
                if (lastId == null) {
                    nextId = 1
                } else {
                    nextId = lastId.toInt() + 1
                }

                val lastPos = realm.where<Exercise>().equalTo("executionDay", selectedExecutionDay).max("position")
                val nextPos: Int
                if (lastPos == null) {
                    nextPos = 0
                } else {
                    nextPos = lastPos.toInt() + 1
                }

                val newExercise = realm.createObject<Exercise>(nextId)
                newExercise.name = name
                newExercise.weight = weightValue
                newExercise.minutesOfRest = minutesOfRestValue
                newExercise.secondsOfRest = secondOfRestValue
                newExercise.series = seriesValue
                newExercise.repetitions = repetitionsValue
                newExercise.executionDay = selectedExecutionDay
                newExercise.position = nextPos

                exercise = newExercise
            }
        }
    }

    override fun onBackPressed() {
        showConfirmDialog()
    }
}
