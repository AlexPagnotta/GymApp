package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_service_exercise.*
import kotlinx.android.synthetic.main.fragment_service_exercise.view.*


class ServiceStartFragment : Fragment() {

    lateinit var exercise:Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val realm = Realm.getDefaultInstance()
        if(arguments!=null){
            val exerciseId = arguments!!.getLong("exerciseId")
            //Get Exercise
            exercise =
                    realm.where<Exercise>().equalTo("id", exerciseId).findFirst()!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_service_start, container, false)
        return root
    }
    companion object {
        fun newInstance(): ServiceStartFragment = ServiceStartFragment()
    }

}
