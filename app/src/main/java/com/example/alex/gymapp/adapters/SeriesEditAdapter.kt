package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.EditExerciseActivity
import com.example.alex.gymapp.R
import com.example.alex.gymapp.extensions.onChange
import com.example.alex.gymapp.model.Series
import kotlinx.android.synthetic.main.series_edit_item.view.*

class SeriesEditAdapter(
        private val items: MutableList<Series>,
        private val context: Context,
        private val parentActivity: EditExerciseActivity
) : RecyclerView.Adapter<SeriesEditViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesEditViewHolder, position: Int) {
        val item = items[holder.adapterPosition]

        //Set series data and add tag to avoid setting pending changes
        holder.weightET.tag = ""
        holder.weightET.setText(item.weight.toString())
        holder.weightET.tag = null

        holder.repetitionsET.tag = ""
        holder.repetitionsET.setText(item.repetitions.toString())
        holder.repetitionsET.tag = null

        holder.removeBtn.setOnClickListener {
            //Remove series
            items.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            parentActivity.hasPendingChanges = true
        }

        //Set minimum and maximum value of edit texts

        //Weight ET
        holder.weightET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Double.parseDouble(it)
                if (number > 1000) {
                    holder.weightET.text!!.replace(0, it.length, "999", 0, 3)
                }
                item.weight = number
            }

            //Set pending changes if tag is null, so it if not the first time loading
            if (holder.weightET.tag == null) parentActivity.hasPendingChanges = true
        }
        //repetitions ET
        holder.repetitionsET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    holder.repetitionsET.text!!.replace(0, it.length, "99", 0, 2)
                } else if (number < 1) {
                    holder.repetitionsET.text!!.replace(0, it.length, "1", 0, 1)
                }

                item.repetitions = number
            }

            //Set pending changes if tag is null, so it if not the first time loading
            if (holder.repetitionsET.tag == null) parentActivity.hasPendingChanges = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesEditViewHolder {
        return com.example.alex.gymapp.adapters.SeriesEditViewHolder(LayoutInflater.from(context).inflate(R.layout.series_edit_item, parent, false))
    }
}

class SeriesEditViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val repetitionsET = view.repetitionsET!!
    val weightET = view.weightET!!
    val removeBtn = view.removeSeriesBtn!!
}



