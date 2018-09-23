package com.example.alex.gymapp

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import android.widget.ArrayAdapter
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.fragment_add_exercise_fragment_dialog.*
import com.example.alex.gymapp.extensions.onChange

class AddExerciseFragmentDialog : BottomSheetDialogFragment() {

    lateinit var selectedExecutionDay : String
    lateinit var parent : ScheduleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parent = parentFragment as ScheduleFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_exercise_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupExecutionDaySpinner()

        validateEditTexts()

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        confirmBtn.setOnClickListener{
            if(isNameETValid()){
                saveToRealm()
                dialog.dismiss()
            }
        }
    }

    private fun setupExecutionDaySpinner(){
        val weekDays = resources.getStringArray(R.array.days_of_week)
        selectedExecutionDay = weekDays[0]

        val dataAdapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, weekDays)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        executionDaySpinner.adapter = dataAdapter

        executionDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                selectedExecutionDay = parentView.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun validateEditTexts(){

        //name ET
        nameET.onChange {
            if(it.isNullOrEmpty()){
                nameIL.error = "The name cannot be empty"
            }
            else{
                nameIL.error = ""
            }
        }

        //Weight ET
        weightET.onChange {
            if(it.isNullOrEmpty()){
                weightET.setText("0")
                weightET.selectAll()
            }
            else{
                val number = java.lang.Double.parseDouble(it)
                if (number > 1000) {
                    weightET.text!!.replace(0, it.length, "1000", 0, 4)
                }
            }
        }

        //MinutesRest ET
        minutesRestET.onChange {
            if(it.isNullOrEmpty()){
                minutesRestET.setText("1")
                minutesRestET.selectAll()
            }
            else{
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    minutesRestET.text!!.replace(0, it.length, "60", 0, 2)
                }
            }
        }

        //SecondsRest ET
        secondsRestET.onChange {
            if(it.isNullOrEmpty()){
                secondsRestET.setText("1")
                secondsRestET.selectAll()
            }
            else{
                val number = java.lang.Integer.parseInt(it)
                if (number > 60) {
                    secondsRestET.text!!.replace(0, it.length, "60", 0, 2)
                }
            }
        }

        //series ET
        seriesET.onChange {
            if(it.isNullOrEmpty()){
                seriesET.setText("1")
                seriesET.selectAll()
            }
            else{
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    seriesET.text!!.replace(0, it.length, "100", 0, 3)
                }
            }
        }

        //repetitions ET
        repetitionsET.onChange {
            if(it.isNullOrEmpty()){
                repetitionsET.setText("1")
                repetitionsET.selectAll()
            }
            else{
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    repetitionsET.text!!.replace(0, it.length, "100", 0, 3)
                }
            }
        }
    }

    private fun isNameETValid() : Boolean{
        if(nameET.text.isNullOrEmpty()){
            nameIL.error = "The name cannot be empty"
            return false
        }

        nameIL.error = ""
        return true
    }

    private fun saveToRealm(){
        val realm = Realm.getDefaultInstance()

        var name = nameET.text.toString()

        var weightValue = java.lang.Double.parseDouble(weightET.text.toString())
        var minutesOfRestValue = java.lang.Integer.parseInt(minutesRestET.text.toString())
        var secondOfRestValue = java.lang.Integer.parseInt(secondsRestET.text.toString())
        var seriesValue = java.lang.Integer.parseInt(seriesET.text.toString())
        var repetitionsValue =  java.lang.Integer.parseInt(repetitionsET.text.toString())

        var exerciseAdded : Exercise? = null

        realm.executeTransaction { realm ->
            val currentIdNum = realm.where<Exercise>().max("id")
            val nextId: Int
            if (currentIdNum == null) {
                nextId = 1
            } else {
                nextId = currentIdNum!!.toInt() + 1
            }

            var exercise = realm.createObject<Exercise>(nextId)
            exercise.name = name
            exercise.weight = weightValue
            exercise.minutesOfRest = minutesOfRestValue
            exercise.secondsOfRest = secondOfRestValue
            exercise.series = seriesValue
            exercise.repetitions = repetitionsValue
            exercise.executionDay = selectedExecutionDay

            exerciseAdded = exercise
        }

        parent.exerciseAdded(exerciseAdded!!)
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
