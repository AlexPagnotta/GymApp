package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import com.example.alex.gymapp.model.Series
import io.realm.RealmList

class SeriesAdapter(
        private val items : List<Series>,
        private val context: Context
) : RecyclerView.Adapter<SeriesViewHolder>()
{
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return com.example.alex.gymapp.adapters.SeriesViewHolder(LayoutInflater.from(context).inflate(R.layout.series_item, parent, false))
    }
}

class SeriesViewHolder (view: View) : RecyclerView.ViewHolder(view) {
}



