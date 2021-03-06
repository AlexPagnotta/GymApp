package com.example.alex.gymapp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : Fragment() {

    var lastWeight: Double = 0.0
    var weightLost: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        updateWeights()

        //Get navigation view from activity
        val navigationView = activity!!.findViewById<TextView>(R.id.navigationView) as BottomNavigationView

        //Go to weights o fab click
        show_weights_fab.setOnClickListener{
            navigationView.setSelectedItemId(R.id.navigation_weights)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        //Set colored title
        val text = "<font color=#279AFF>Gym</font><font color=#757575>App</font>"
        toolbar.setTitle(Html.fromHtml(text))
    }


    //Update last weight when fragment is visible
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            updateWeights();
        }
    }

    private fun updateWeights(){

        val realm = Realm.getDefaultInstance()

        val weights = realm.where<Weight>().sort("dateOfWeight", Sort.DESCENDING).findAll()

        //TODO check if there is at least one weight
        if(weights.count() == 1){
            lastWeight = weights.first()!!.weight
            weightLost = lastWeight
        }
        else if(weights.count() != 0){
            lastWeight = weights.first()!!.weight
            val secondLastWeight = weights[1]!!.weight

            weightLost = lastWeight - secondLastWeight
        }

        weightTW.text = String.format("%1$,.2f Kg", lastWeight)

        weightLostTW.text = String.format("You Have Lost %1$,.2f Kg", weightLost)
    }

    companion object {
        fun newInstance(): MeFragment = MeFragment()
    }
}
