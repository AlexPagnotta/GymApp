package com.example.alex.gymapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alex.gymapp.R
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import com.example.alex.gymapp.ExerciseActivity
import com.example.alex.gymapp.helpers.ItemTouchHelperAdapter
import com.example.alex.gymapp.model.Exercise
import kotlinx.android.synthetic.main.exercise_item.view.*
import java.util.*
import java.util.Collections.swap
import android.view.MotionEvent
import android.support.v4.view.MotionEventCompat
import android.view.View.OnTouchListener

class ExerciseAdapter(
        private val items : RealmResults<Exercise>,
        private val context: Context,
        private val parentFragment: Fragment
    ) : RealmRecyclerViewAdapter<Exercise, ExerciseViewHolder>(items, true), ItemTouchHelperAdapter
{
    private val selectedItems: ArrayList<Exercise> = ArrayList()
    private var isSelectionMode: Boolean = false
    private lateinit var receiver: OnClickAction
    private lateinit var dragStartListener: OnStartDragListener

    interface OnClickAction {
        fun onClickAction()
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {

        val item = items[position]

        //Show Name
        var name = item!!.name
        if(name.isEmpty()){
            name = "No Exercise Name"
        }
        holder.nameTV.text = name

        //Show Series Repetition
        val seriesRepetitionsString = String.format("%dx%d", item.series, item.repetitions )
        holder.seriesRepetitionTV.text = seriesRepetitionsString

        //Show Weight
        val restString = String.format("%d Min.\n%d Sec.", item.minutesOfRest, item.secondsOfRest)
        holder.restTimeTV.text = restString

        //Show Weight
        val weightString = String.format("%1$,.2f\nKg", item.weight)
        holder.weightTV.text = weightString

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
            else{
                val item = items.get(position)

                val intent = Intent(context, ExerciseActivity::class.java)
                intent.putExtra("exerciseId",  item!!.id)
                parentFragment.startActivityForResult(intent, 2)
            }
        }

        if (selectedItems.contains(item))
            selectView(holder)
        else
            deselectView(holder)

        holder.handleImg.setOnTouchListener(OnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(holder)
            }
            false
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return com.example.alex.gymapp.adapters.ExerciseViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false))
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

    fun setDragStartListener(dragStartListener: OnStartDragListener) {
        this.dragStartListener = dragStartListener
    }

    fun clearSelected() {
        selectedItems.clear()
        notifyDataSetChanged()
        isSelectionMode = false
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        /*if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
                items.move()
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }*/
        notifyItemMoved(fromPosition, toPosition)
    }

}

class ExerciseViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews
    val nameTV = view.nameTV!!
    val restTimeTV = view.restTimeTV!!
    val seriesRepetitionTV = view.seriesRepetitionsTV!!
    val weightTV = view.weightTV!!
    val handleImg = view.handleImg!!
}



