package com.example.alex.gymapp

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.*
import android.widget.ArrayAdapter
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.fragment_add_exercise_fragment_dialog.*
import android.widget.TextView
import javax.security.auth.callback.Callback
import android.text.Editable
import android.text.TextWatcher






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

        val weekDays = resources.getStringArray(R.array.days_of_week)

        selectedExecutionDay = weekDays[0]

        val dataAdapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,weekDays)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        executionDaySpinner.adapter = dataAdapter

        executionDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>,
                                        selectedItemView: View, position: Int, id: Long) {
                selectedExecutionDay = parentView.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }

        secondsRestET.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    val `val` = Integer.parseInt(s.toString())
                    if (`val` > 60) {
                        s.replace(0, s.length, "60", 0, 2)
                    } else if (`val` < 1) {
                        s.replace(0, s.length, "0", 0, 1)
                    }
                } catch (ex: NumberFormatException) {
                    s.replace(0, s.length, "0", 0, 1)
                }

            }
        })

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        confirmBtn.setOnClickListener{

            val realm = Realm.getDefaultInstance()

            var name = nameET.text.toString()

            var weightValue = 0.0
            var minutesOfRestValue = 0
            var secondOfRestValue = 0
            var seriesValue = 0
            var repetionsValue = 0


            val weight = weightET.text.toString()
            if (!weight.isEmpty()) {
                try {
                    weightValue = java.lang.Double.parseDouble(weight)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val minutesOfRest = minutesRestET.text.toString()
            if (!minutesOfRest.isEmpty()) {
                try {
                    minutesOfRestValue = java.lang.Integer.parseInt(minutesOfRest)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val secondsOfRest = secondsRestET.text.toString()
            if (!secondsOfRest.isEmpty()) {
                try {
                    secondOfRestValue = java.lang.Integer.parseInt(secondsOfRest)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val series = seriesET.text.toString()
            if (!series.isEmpty()) {
                try {
                    seriesValue = java.lang.Integer.parseInt(series)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val repetitions = repetitionsET.text.toString()
            if (!repetitions.isEmpty()) {
                try {
                    repetionsValue = java.lang.Integer.parseInt(repetitions)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            var exerciseAdded : Exercise? = null

            realm.executeTransaction { realm ->
                // Add a person
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
                exercise.repetitions = repetionsValue
                exercise.executionDay = selectedExecutionDay

                exerciseAdded = exercise
            }

            parent.exerciseAdded(exerciseAdded!!)

            dialog.dismiss()
        }
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
