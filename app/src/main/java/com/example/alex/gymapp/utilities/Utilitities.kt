package com.example.alex.gymapp.utilities

import android.content.Context
import com.example.alex.gymapp.R

object Utilities {

    fun getExecutionDayString(context: Context, executionDay: Int): String {
        val weekDays = context.resources.getStringArray(R.array.days_of_week)
        return weekDays[executionDay]
    }

}