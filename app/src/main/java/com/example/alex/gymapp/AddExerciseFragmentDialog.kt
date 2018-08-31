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
import kotlinx.android.synthetic.main.fragment_add_exercise_fragment_dialog.*
import java.util.*
import android.widget.ArrayAdapter



class AddExerciseFragmentDialog : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_add_exercise_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //executionDaySpinner.onItemClickListener = this as AdapterView.OnItemClickListener

        val weekDays = ArrayList<String>()
        weekDays.add("Monday")
        weekDays.add("Tuesday")
        weekDays.add("Wednesday")
        weekDays.add("Thursday")
        weekDays.add("Friday")
        weekDays.add("Saturday")
        weekDays.add("Sunday ")

        val dataAdapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,weekDays)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        executionDaySpinner.adapter = dataAdapter

        /*cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        //Set current date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        datePicker.init(year,month,day,null)

        confirmBtn.setOnClickListener{

            val realm = Realm.getDefaultInstance()

            realm.executeTransaction { realm ->
                // Add a person
                val currentIdNum = realm.where<Weight>().max("id")
                val nextId: Int
                if (currentIdNum == null) {
                    nextId = 1
                } else {
                    nextId = currentIdNum!!.toInt() + 1
                }
                val weight = realm.createObject<Weight>(nextId)
                weight.weight = weightValue
                weight.dateOfWeight = dateOfWeight
            }

            dialog.dismiss()
        }*/
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
