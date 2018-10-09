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

class AddWeightFragmentDialog : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_weight_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        confirmBtn.setOnClickListener{
            saveToRealm()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }

        //Set 0 if empty
        weightET.onChange {
            if(it.isNullOrEmpty()){
                weightET.setText("0")
                weightET.selectAll()
            }
        }

        weightET.setSelectAllOnFocus(true)

        // Open the soft keyboard at start
        weightET.requestFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        setTimePickerCurrentDate()
    }

    private fun setTimePickerCurrentDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        datePicker.init(year,month,day,null)
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

        var weightValue: Double

        val text = weightET.text.toString()
        weightValue = java.lang.Double.parseDouble(text)

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
