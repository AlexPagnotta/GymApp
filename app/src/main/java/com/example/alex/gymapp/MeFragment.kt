package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Html
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
        val weights = realm.where<Weight>().findAll().sort("dateOfWeight",Sort.DESCENDING)

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


            setupChart(weights)

            val lastWeight = weights.first()!!.weight
            weightTW.text =  String.format("%1$,.2f Kg",lastWeight)
        }

        //Get all days
        val weekDaysArray = resources.getStringArray(R.array.days_of_week)
        val c = Calendar.getInstance()
        val today = c.get(Calendar.DAY_OF_WEEK) - 1 //+1 to adjust indexes
        //Load today exercise
        val exercisesOfDayRealm = realm.where<Exercise>().equalTo("executionDay", today).findAll()

        if(exercisesOfDayRealm.count() == 0){
            goToScheduleFab.visibility = View.GONE
            scheduleBannerTW.text = "No schedule for today"
        }
        else{
            goToScheduleFab.visibility = View.VISIBLE
            scheduleBannerTW.text = "Go to today schedule"

            goToScheduleFab.setOnClickListener{
                //Start the schedule of the selected day
                (activity!! as MainActivity).startScheduleOnCurrentDay = true
                val navigationView = activity!!.findViewById<TextView>(R.id.navigationView) as BottomNavigationView
                navigationView.setSelectedItemId(R.id.navigation_schedule)
            }
        }
    }

    private fun setupChart(weights: RealmResults<Weight>) {
        var set = LineSet()
        for (weight in weights) {
            var p = Point("day",weight.weight.toFloat())
            p.radius = 20f
            p.strokeColor = Color.parseColor("#C8C8C8")
            p.isVisible = true
            p.color = Color.WHITE
            set.addPoint(p)
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
        var anim = Animation()
        weightChart.show(anim)
    }

    companion object {
        fun newInstance(): MeFragment = MeFragment()
    }
}
