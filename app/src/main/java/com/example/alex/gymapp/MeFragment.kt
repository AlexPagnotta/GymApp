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
import com.db.chart.model.ChartEntry
import com.db.chart.model.ChartSet
import com.db.chart.model.LineSet
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_me.*
import android.R.attr.action
import android.R.attr.action
import android.graphics.Color
import com.db.chart.animation.Animation
import com.db.chart.model.Point
import com.db.chart.renderer.AxisRenderer


class MeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        //Set colored title
        val text = "<font color=#279AFF>Gym</font><font color=#757575>App</font>"
        toolbar.setTitle(Html.fromHtml(text))

        val realm = Realm.getDefaultInstance()
        //Load weights
        val weights = realm.where<Weight>().findAll().sort("dateOfWeight",Sort.DESCENDING)

        var set = LineSet()

        for (weight in weights) {
            var p = Point("day",weight.weight.toFloat())
            p.radius = 20f
            p.strokeColor = Color.WHITE
            p.isVisible = true
            p.color = Color.BLUE
            set.addPoint(p)
        }

        set.setColor(Color.WHITE)
                .setFill(Color.BLUE)


        weightChart.addData(set)

        weightChart.setLabelsColor(Color.WHITE)
        weightChart.setAxisLabelsSpacing(50)
        weightChart.setFontSize(60)
        weightChart.setBackgroundColor(Color.parseColor("#279aff"))
        weightChart.setXAxis(false)
        weightChart.setYAxis(false)
        weightChart.setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
        weightChart.setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
        weightChart.setStep(10)
        var anim = Animation()
        weightChart.show(anim)



    }

    companion object {
        fun newInstance(): MeFragment = MeFragment()
    }
}
