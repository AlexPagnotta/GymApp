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

        val weights = realm.where<Weight>().findAll().toList()

        var recyclerView = view.findViewById(R.id.weightsRW) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(context!!)

        recyclerView.adapter = WeightAdapter(weights, context!!)
    }

    companion object {
        fun newInstance(): WeightsFragment = WeightsFragment()
    }
}
