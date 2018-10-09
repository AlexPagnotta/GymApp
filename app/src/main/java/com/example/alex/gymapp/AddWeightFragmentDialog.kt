package com.example.alex.gymapp

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.text.TextUtils
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
import com.example.alex.gymapp.extensions.onChange
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import android.support.v4.content.ContextCompat.getSystemService
import com.example.alex.gymapp.R.id.datePicker

class AddWeightFragmentDialog : BottomSheetDialogFragment() {

    lateinit var realm: Realm
    lateinit var weight: Weight

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_weight_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setEditTextsMinMax();

        confirmBtn.setOnClickListener{
            saveToRealm()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        setTimePickerCurrentDate()
    }

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
    }

    private fun setTimePickerCurrentDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dateSPicker.init(year,month,day,null)
    }

    private fun getDateFromDatePicker(datePicker: DatePicker): Date {
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return calendar.time
    }

    private fun saveToRealm(){

        val realm = Realm.getDefaultInstance()

        var weightValue = 0.0
        try{
            weightValue = java.lang.Double.parseDouble(weightET.text.toString())
        }
        catch (e: Exception){ }


        val dateOfWeight = getDateFromDatePicker(datePicker)

        realm.executeTransaction { realm ->
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
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }

    companion object {
        fun newInstance(): AddWeightFragmentDialog = AddWeightFragmentDialog()
    }
}
