package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_start_schedule.*

class StartScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get the selected day
        if(arguments != null){
            val executionDay = arguments!!.getInt("currentExecutionDay")

            reloadUi(executionDay)
        }
    }

    private fun reloadUi(executionDay:Int){
        scheduleTitleTw.text = String.format("Schedule of %s",executionDay)
    }

    companion object {
        fun newInstance(): StartScheduleFragment = StartScheduleFragment()
    }
}
