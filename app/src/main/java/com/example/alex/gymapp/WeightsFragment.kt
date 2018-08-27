package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.adapters.WeightAdapter
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_weights.*

class WeightsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weights, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val realm = Realm.getDefaultInstance()

        val weights = realm.where<Weight>().findAll()

        var recyclerView = view.findViewById(R.id.weightsRW) as RecyclerView

        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        recyclerView.layoutManager = lm

        recyclerView
        recyclerView.adapter = WeightAdapter(weights, context!!)

        //Go to weights o fab click
        add_weight_fab.setOnClickListener{
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = AddWeightFragmentDialog()
            dialogFragment.show(ft, "dialog")
        }
    }

    companion object {
        fun newInstance(): WeightsFragment = WeightsFragment()
    }
}

