package com.example.alex.gymapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_add_weight_fragment_dialog.*
import java.util.*

import android.widget.DatePicker

class AddWeightFragmentDialog : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.fragment_add_weight_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cancelBtn.setOnClickListener{
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

            var weightValue = 0.0

            val text = weightET.text.toString()
            if (!text.isEmpty()) {
                weightValue = java.lang.Double.parseDouble(text)

                val dateOfWeight = getDateFromDatePicket(datePicker)

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
            }

            else{
                weightIL.error = "Cannot be empty"
            }
        }
    }

    fun getDateFromDatePicket(datePicker: DatePicker): Date {
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return calendar.time
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
