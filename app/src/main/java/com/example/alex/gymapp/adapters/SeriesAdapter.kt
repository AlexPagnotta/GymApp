package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import com.example.alex.gymapp.model.Series
import kotlinx.android.synthetic.main.series_item.view.*

class SeriesAdapter(
        private val items: MutableList<Series>,
        private val context: Context
) : RecyclerView.Adapter<SeriesViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val item = items[holder.adapterPosition]

        holder.weightTW.text = String.format("%1$,.2f Kg", item.weight)
        holder.repetitionsTW.text =String.format("%d ", item.repetitions )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return com.example.alex.gymapp.adapters.SeriesViewHolder(LayoutInflater.from(context).inflate(R.layout.series_item, parent, false))
    }
}

class SeriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val repetitionsTW = view.repetitionsTW2!!
    val weightTW = view.weightTW2!!
}



