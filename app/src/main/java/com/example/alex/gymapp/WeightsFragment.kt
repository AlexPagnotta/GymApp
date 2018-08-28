package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.alex.gymapp.adapters.WeightAdapter
import com.example.alex.gymapp.model.Weight
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_weights.*
import android.widget.Toast
import com.example.alex.gymapp.adapters.WeightAdapter.OnClickAction
import android.app.Activity

class WeightsFragment : Fragment(), WeightAdapter.OnClickAction {

    var actionMode: ActionMode? = null
    lateinit var adapter: WeightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weights, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val realm = Realm.getDefaultInstance()

        val weights = realm.where<Weight>().findAll()

        var recyclerView = view.findViewById(R.id.weightsRW) as RecyclerView

        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        recyclerView.layoutManager = lm

        adapter = WeightAdapter(weights, context!!)

        recyclerView.adapter = adapter

        var oco = this as WeightAdapter.OnClickAction
        adapter.setActionModeReceiver(oco)


        //Go to weights o fab click
        add_weight_fab.setOnClickListener{
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = AddWeightFragmentDialog()
            dialogFragment.show(ft, "dialog")
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.getMenuInflater()
            inflater.inflate(R.menu.weight_multi_selection, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_delete -> {
                    mode.finish()
                    return true
                }
                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
        }
    }

    override fun onClickAction() {
        val selected = adapter.getSelectedItems().size
        if (actionMode == null) {
            actionMode = (context as Activity).startActionMode(actionModeCallback)
            actionMode?.title = "Selected: $selected"
        } else {
            if (selected == 0) {
                actionMode?.finish()
            } else {
                actionMode?.title = "Selected: $selected"
            }
        }
    }


    companion object {
        fun newInstance(): WeightsFragment = WeightsFragment()
    }
}

