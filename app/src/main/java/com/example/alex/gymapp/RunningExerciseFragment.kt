package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.fragment_running_exercise.*

class RunningExerciseFragment : Fragment() {

    lateinit var exercise:Exercise

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exerciseId = arguments!!.getLong("exerciseId")
        nameTw.text = exerciseId.toString()
    }

    companion object {
        fun newInstance(): RunningExerciseFragment = RunningExerciseFragment()
    }

}
