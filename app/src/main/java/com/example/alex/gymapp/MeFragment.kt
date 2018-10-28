package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.chart.model.LineSet
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_me.*
import android.graphics.Color
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import com.db.chart.animation.Animation
import com.db.chart.model.Point
import com.db.chart.renderer.AxisRenderer
import com.example.alex.gymapp.model.Exercise
import io.realm.RealmResults
import java.util.*


class MeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val realm = Realm.getDefaultInstance()

        //Load weights
        val weights = realm.where<Weight>().
                findAll()
                .sort("dateOfWeight",Sort.DESCENDING)

        //Check if there is at least one weight
        if(weights.count() == 0){
            noWeightTW.visibility = View.VISIBLE
            weightTextTW.visibility = View.GONE
            weightTW.visibility = View.GONE
            weightChart.visibility = View.GONE
        }
        else{
            noWeightTW.visibility = View.GONE
            weightTextTW.visibility = View.VISIBLE
            weightTW.visibility = View.VISIBLE
            weightChart.visibility = View.VISIBLE

            //Load the weights chart
            setupChart(weights)

            //Show the last weight
            val lastWeight = weights.first()!!.weight
            weightTW.text =  String.format("%1$,.2f Kg",lastWeight)
        }

        //Get index of today
        val c = Calendar.getInstance()
        val today = c.get(Calendar.DAY_OF_WEEK) - 1 //+1 to adjust indexes
        //Load today exercises
        val exercisesOfDayRealm = realm.where<Exercise>().equalTo("executionDay", today).findAll()

        //If there are no schedule for today
        if(exercisesOfDayRealm.count() == 0){
            goToScheduleFab.visibility = View.GONE
            scheduleBannerTW.text = "No schedule for today"
        }
        else{
            goToScheduleFab.visibility = View.VISIBLE
            scheduleBannerTW.text = "Go to today schedule"

            goToScheduleFab.setOnClickListener{
                //Start the schedule of the selected day on fab click
                (activity!! as MainActivity).startScheduleOnCurrentDay = true
                val navigationView = activity!!.findViewById<TextView>(R.id.navigationView) as BottomNavigationView
                navigationView.selectedItemId = R.id.navigation_schedule
            }
        }
    }

    private fun setupChart(weights: RealmResults<Weight>) {
        val set = LineSet()

        //Get at max 10 weights to show in chart
        val weightsLimit = 10
        var weightsCount = 0

        for (weight in weights) {

            if(weightsCount == weightsLimit) break

            val point = Point("day",weight.weight.toFloat())
            point.radius = 20f
            point.strokeColor = Color.parseColor("#C8C8C8")
            point.isVisible = true
            point.color = Color.WHITE
            set.addPoint(point)

            weightsCount++
        }

        set.setColor(Color.parseColor("#279aff"))
                .setFill(Color.parseColor("#D5EBFF"))
        set.isSmooth =true
        weightChart.addData(set)
        weightChart.setFontSize(60)
        weightChart.setBackgroundColor(Color.WHITE)
        weightChart.setXAxis(false)
        weightChart.setYAxis(false)
        weightChart.setYLabels(AxisRenderer.LabelPosition.NONE)
        weightChart.setXLabels(AxisRenderer.LabelPosition.NONE)
        val anim = Animation()
        weightChart.show(anim)
    }

    companion object {
        fun newInstance(): MeFragment = MeFragment()
    }
}
