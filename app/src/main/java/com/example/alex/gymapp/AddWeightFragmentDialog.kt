package com.example.alex.gymapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_add_weight_fragment_dialog.*

class AddWeightFragmentDialog : DialogFragment() {

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

        confirmBtn.setOnClickListener{

            val realm = Realm.getDefaultInstance()

            var value: Double = 0.0

            val text = weightET.text.toString()
            if (!text.isEmpty()) {
                try {
                    value = java.lang.Double.parseDouble(text)
                    // it means it is double
                } catch (e1: Exception) {
                    // this means it is not double
                    e1.printStackTrace()
                }
            }

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
                weight.weight = value
            }

            dialog.dismiss()
        }
    }


    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
