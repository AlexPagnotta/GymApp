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
import com.example.alex.gymapp.EditWeightFragmentDialog
import com.example.alex.gymapp.MainActivity
import android.os.Bundle

class WeightAdapter(
        private val items : RealmResults<Weight>,
        private val context: Context
    ) : RealmRecyclerViewAdapter<Weight, ViewHolder>(items, true)
{
    private val selectedItems: ArrayList<Weight> = ArrayList()
    private var isSelectionMode: Boolean = false
    private lateinit var receiver: OnClickAction

    interface OnClickAction {
        fun onClickAction()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Show Weight
        val item = items.get(position)
        val weightString = String.format("%1$,.2f Kg", item!!.weight)
        holder.weightTw.text = weightString;

        //Show Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = item.dateOfWeight
        val dateString = dateFormat.format(date)
        holder.dateTw.text = dateString

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
                val ft = (context as MainActivity).supportFragmentManager.beginTransaction()
                val prev = (context).supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val dialogFragment = EditWeightFragmentDialog()
                val args = Bundle()
                args.putLong("weightId", item.id)
                dialogFragment.arguments = args
                dialogFragment.show(ft, "dialog")
            }
        }

        if (selectedItems.contains(item))
            selectView(holder)
        else
            deselectView(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return com.example.alex.gymapp.adapters.ViewHolder(LayoutInflater.from(context).inflate(R.layout.weights_item, parent, false))
    }

    private fun selectView(holder: ViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelection))
    }

    private fun deselectView(holder: ViewHolder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

    fun getSelectedItems(): ArrayList<Weight> {
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

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews
    val weightTw = view.weightTW!!
    val dateTw = view.dateTW!!
}

