package com.example.alex.gymapp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : Fragment() {

    var lastWeight: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val realm = Realm.getDefaultInstance()

        val persons = realm.where<Weight>().findAll()

        //TODO check if there is at least one weight
        lastWeight = persons.max("weight")!!.toDouble()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_me, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        weightTW.text = lastWeight.toString()

        val navigationView = activity!!.findViewById<TextView>(R.id.navigationView) as BottomNavigationView

        show_weights_fab.setOnClickListener{
            navigationView.setSelectedItemId(R.id.navigation_weights)
        }
    }

    companion object {
        fun newInstance(): MeFragment = MeFragment()
    }
}
