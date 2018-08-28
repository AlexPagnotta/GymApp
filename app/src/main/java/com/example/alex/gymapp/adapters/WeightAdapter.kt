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
import android.support.v4.content.ContextCompat

class WeightAdapter(
            val items : RealmResults<Weight>,
            val context: Context
    ) : RealmRecyclerViewAdapter<Weight, ViewHolder>(items, true)
{
    val selectedItems: ArrayList<Weight> = ArrayList()
    var isSelectionMode: Boolean = false

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        val weightString = String.format("%1$,.2f Kg", item!!.weight);
        holder?.weight?.text = weightString;

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = item!!.dateOfWeight
        val dateString = dateFormat.format(date)
        holder?.date?.text = dateString

        //Selection
        holder.itemView.setOnLongClickListener {
            if(!isSelectionMode){
                isSelectionMode = true
                selectedItems.add(item)
                highlightView(holder)
            }
            true
        }

        holder.itemView.setOnClickListener() {
            if(isSelectionMode){
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                    unhighlightView(holder)
                } else {
                    selectedItems.add(item)
                    highlightView(holder)
                }

                if(selectedItems.count() == 0)
                    isSelectionMode = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.weights_item, parent, false))
    }

    private fun highlightView(holder: ViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelection))
    }

    private fun unhighlightView(holder: ViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView
    val weight = view.weightTW
    val date = view.dateTW
}

