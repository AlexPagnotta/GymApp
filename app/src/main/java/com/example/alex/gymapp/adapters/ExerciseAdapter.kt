package com.example.alex.gymapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import android.support.v4.content.ContextCompat
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExerciseAdapter(
        private val items : RealmResults<Exercise>,
        private val context: Context
    ) : RealmRecyclerViewAdapter<Exercise, ExerciseViewHolder>(items, true)
{
    private val selectedItems: ArrayList<Exercise> = ArrayList()
    private var isSelectionMode: Boolean = false
    private lateinit var receiver: OnClickAction

    interface OnClickAction {
        fun onClickAction()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {

        val item = items.get(position)

        //Show Name
        holder?.nameTV.text = item!!.name

        //Show Series Repetition
        val seriesRepetitionsString = String.format("%dx%d ", item!!.series,item!!.repetitions )
        holder?.seriesRepetitionTV?.text = seriesRepetitionsString

        //Show Weight
        val restString = String.format("%dM %dS", item!!.minutesOfRest, item!!.secondsOfRest)
        holder?.restTimeTV?.text = restString

        //Show Weight
        val weightString = String.format("%1$,.2f Kg", item!!.weight)
        holder?.weightTV?.text = weightString

        //Manage Selection
        holder.itemView.setOnLongClickListener {
            if(!isSelectionMode){
                isSelectionMode = true
                selectedItems.add(item)
                selectView(holder)

                receiver.onClickAction()
            }
            true
        }

        holder.itemView.setOnClickListener {
            if(isSelectionMode){
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                    deselectView(holder)
                } else {
                    selectedItems.add(item)
                    selectView(holder)
                }

                if(selectedItems.count() == 0)
                    isSelectionMode = false

                receiver.onClickAction()
            }
        }

        if (selectedItems.contains(item))
            selectView(holder)
        else
            deselectView(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false))
    }

    private fun selectView(holder: ExerciseViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelection))
    }

    private fun deselectView(holder: ExerciseViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

    fun getSelectedItems(): ArrayList<Exercise> {
        return selectedItems
    }

    fun setActionModeReceiver(receiver: OnClickAction) {
       this.receiver = receiver
    }

    fun clearSelected() {
        selectedItems.clear()
        notifyDataSetChanged()
        isSelectionMode = false
    }

}

class ExerciseViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews
    val nameTV = view.nameTV!!
    val restTimeTV = view.restTimeTV!!
    val seriesRepetitionTV = view.seriesRepetitionsTV!!
    val weightTV = view.weightTV!!

}

