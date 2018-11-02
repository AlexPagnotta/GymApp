package com.example.alex.gymapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.alex.gymapp.R
import com.example.alex.gymapp.model.Series
import io.realm.RealmList
import io.realm.RealmRecyclerViewAdapter

class SeriesAdapter(
        val items : RealmList<Series>,
        val context: Context
) : RealmRecyclerViewAdapter<Series, ViewHolder>(items, true)
{
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder?.?.text = items.get(position)!!.weight.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.series_item, parent, false))
    }
}