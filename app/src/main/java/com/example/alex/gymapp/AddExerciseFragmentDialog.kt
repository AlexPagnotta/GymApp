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
import java.util.*
import android.widget.ArrayAdapter
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.fragment_add_exercise_fragment_dialog.*
import android.widget.TextView




class AddExerciseFragmentDialog : BottomSheetDialogFragment() {

    lateinit var selectedExecutionDay : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        confirmBtn.setOnClickListener{

            val realm = Realm.getDefaultInstance()

            var name = nameET.text.toString()

            var weightValue = 0.0
            var restTimeValue = 0.0
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

            val restTime = restTimeET.text.toString()
            if (!restTime.isEmpty()) {
                try {
                    restTimeValue = java.lang.Double.parseDouble(restTime)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val series = weightET.text.toString()
            if (!series.isEmpty()) {
                try {
                    seriesValue = java.lang.Integer.parseInt(series)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            val repetitions = restTimeET.text.toString()
            if (!repetitions.isEmpty()) {
                try {
                    repetionsValue = java.lang.Integer.parseInt(repetitions)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }

            realm.executeTransaction { realm ->
                // Add a person
                val currentIdNum = realm.where<Exercise>().max("id")
                val nextId: Int
                if (currentIdNum == null) {
                    nextId = 1
                } else {
                    nextId = currentIdNum!!.toInt() + 1
                }
                val exercise = realm.createObject<Exercise>(nextId)
                exercise.name = name
                exercise.weight = weightValue
                exercise.restTime = restTimeValue
                exercise.series = seriesValue
                exercise.repetitions = repetionsValue
                exercise.executionDay = selectedExecutionDay
            }

            dialog.dismiss()
        }
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
