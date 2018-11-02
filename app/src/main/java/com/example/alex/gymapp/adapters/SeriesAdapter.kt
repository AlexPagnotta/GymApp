package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import com.example.alex.gymapp.extensions.onChange
import com.example.alex.gymapp.model.Series
import kotlinx.android.synthetic.main.series_item.view.*

class SeriesAdapter(
        private val items : MutableList<Series>,
        private val context: Context
) : RecyclerView.Adapter<SeriesViewHolder>()
{
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val item = items[position]

        //Set series data and add tag to avoid setting pending changes
        holder.weightET.tag = ""
        holder.weightET.setText(item.weight.toString())
        holder.weightET.tag = null

        holder.repetitionsET.tag = ""
        holder.repetitionsET.setText(item.repetitions.toString())
        holder.repetitionsET.tag = null

        holder.removeBtn.setOnClickListener{
            items.removeAt(position)
            //Not working correctly reimplement
            //notifyItemRemoved(position)
            notifyDataSetChanged()
        }

        //Weight ET
        holder.weightET.onChange {
            if(!it.isEmpty()) {
                val number = java.lang.Double.parseDouble(it)
                if (number > 1000) {
                    holder.weightET.text!!.replace(0, it.length, "999", 0, 3)
                }

                item.weight = number
            }

            //TODO Reimplement
            //if (holder.weightET.tag == null) hasPendingChanges = true
        }
        //repetitions ET
        holder.repetitionsET.onChange {
            if (!it.isEmpty()) {
                val number = java.lang.Integer.parseInt(it)
                if (number > 100) {
                    holder.repetitionsET.text!!.replace(0, it.length, "99", 0, 2)
                }
                else if(number < 1){
                    holder.repetitionsET.text!!.replace(0, it.length, "1", 0, 1)
                }

                item.repetitions = number
            }

            //TODO Reimplement
            //if (holder.repetitionsET.tag == null)  hasPendingChanges = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return com.example.alex.gymapp.adapters.SeriesViewHolder(LayoutInflater.from(context).inflate(R.layout.series_item, parent, false))
    }
}

class SeriesViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val repetitionsET = view.repetitionsET!!
    val weightET = view.weightET!!
    val removeBtn = view.removeSeriesBtn!!
}



