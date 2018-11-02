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
import android.support.v7.widget.LinearLayoutManager
import com.example.alex.gymapp.adapters.SeriesAdapter
import com.example.alex.gymapp.extensions.onChange
import com.example.alex.gymapp.model.Series
import io.realm.kotlin.createObject
import android.widget.ScrollView



class EditExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise
    private lateinit var seriesList: MutableList<Series>

    lateinit var adapter: SeriesAdapter

    var selectedExecutionDay = 0

    var hasPendingChanges = false

    //Bool to indicate if it's a new exercise on an existing one
    var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercise)

        realm = Realm.getDefaultInstance()

        seriesList =  mutableListOf()

        //If the id is present is an existing note
        if(intent.hasExtra("exerciseId")){
            //Get Exercise
            val exerciseId = intent.getLongExtra("exerciseId", 0)
            exercise = realm.where<Exercise>().equalTo("id", exerciseId).findFirst()!!
            //Get series of exercise
            seriesList = realm.copyFromRealm(exercise.series)

            //Set exercise data and add tag to avoid setting pending changes
            nameET.tag = ""
            nameET.setText(exercise.name)
            nameET.tag = null

            minutesRestET.tag = ""
            minutesRestET.setText(exercise.minutesOfRest.toString())
            minutesRestET.tag = null

            secondsRestET.tag = ""
            secondsRestET.setText(exercise.secondsOfRest.toString())
            secondsRestET.tag = null

            selectedExecutionDay = exercise.executionDay

            isEditMode = true
        }
        //Set the schedule day as the current one on the schedule fragment
        else if(intent.hasExtra("currentExecutionDay")) {
            val executionDay = intent.getIntExtra("currentExecutionDay", 0)
            selectedExecutionDay = executionDay
        }

        setupSeriesRecyclerView()
        setupExecutionDaySpinner()
        setupEditTexts()

        addSeriesBtn.setOnClickListener {
            var series = Series()
            if(seriesList.count() != 0){
                var lastSeries = seriesList.last();
                series.weight = lastSeries.weight
                series.repetitions = lastSeries.repetitions
            }
            seriesList.add(series)
            adapter.notifyItemInserted(adapter.itemCount)
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            hasPendingChanges = true
        }

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

    private fun setupSeriesRecyclerView(){
        //Load series into recycler view
        val lm = LinearLayoutManager(this)
        seriesRW.layoutManager = lm
        adapter = SeriesAdapter(seriesList, this, this)
        seriesRW.isNestedScrollingEnabled = false
        seriesRW.adapter = adapter
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
    }

    private fun saveExercise(){

        //Get data
        val name = nameET.text.toString()

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

        val realm = Realm.getDefaultInstance()

        //Update the edited exercise
        if(isEditMode){
            //Update on DB
            realm.executeTransaction { realm ->
                //Update Exercise
                exercise.name = name
                exercise.minutesOfRest = minutesOfRestValue
                exercise.secondsOfRest = secondOfRestValue
                exercise.executionDay = selectedExecutionDay

                //Delete old series of the exercise
                exercise.series.deleteAllFromRealm()

                //Get last id of series
                val lastId = realm.where<Series>().max("id")
                var nextId: Int
                if (lastId == null) {
                    nextId = 1
                } else {
                    nextId = lastId.toInt() + 1
                }

                for (series in seriesList){
                    var realmSeries = realm.createObject<Series>(nextId)
                    realmSeries.repetitions = series.repetitions
                    realmSeries.weight = series.weight
                    exercise.series.add(realmSeries)
                    nextId++
                }

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
                newExercise.minutesOfRest = minutesOfRestValue
                newExercise.secondsOfRest = secondOfRestValue
                newExercise.executionDay = selectedExecutionDay
                newExercise.position = nextPos

                //Get last id of series
                val lastSeriesId = realm.where<Series>().max("id")
                var nextSeriesId: Int
                if (lastSeriesId == null) {
                    nextSeriesId = 1
                } else {
                    nextSeriesId = lastSeriesId.toInt() + 1
                }

                for (series in seriesList){
                    val realmSeries = realm.createObject<Series>(nextSeriesId)
                    realmSeries.repetitions = series.repetitions
                    realmSeries.weight = series.weight
                    newExercise.series.add(series)
                    nextSeriesId++
                }

                exercise = newExercise
            }
        }
    }

    override fun onBackPressed() {
        showConfirmDialog()
    }
}
