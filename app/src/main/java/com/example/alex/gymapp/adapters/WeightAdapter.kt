package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import com.example.alex.gymapp.model.Weight
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.weights_item.view.*
import java.text.SimpleDateFormat


class WeightAdapter(
            val items : RealmResults<Weight>,
            val context: Context
    ) : RealmRecyclerViewAdapter<Weight, ViewHolder>(items, true)
{
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.weight?.text = items.get(position)!!.weight.toString()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = items.get(position)!!.dateOfWeight
        val dateString = dateFormat.format(date)
        holder?.date?.text = dateString
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.weights_item, parent, false))
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView
    val weight = view.weightTW
    val date = view.dateTW
}

