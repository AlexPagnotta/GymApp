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
import android.widget.Toolbar
import io.realm.RealmList
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode;
import android.text.Html
import com.example.alex.gymapp.R.id.toolbar
import android.support.v4.view.ViewCompat.setElevation


class WeightsFragment : Fragment(), WeightAdapter.OnClickAction {

    lateinit var realm: Realm
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
        realm = Realm.getDefaultInstance()

        val weights = realm.where<Weight>().findAll()

        var recyclerView = view.findViewById(R.id.weightsRW) as RecyclerView

        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        recyclerView.layoutManager = lm

        adapter = WeightAdapter(weights, context!!)

        recyclerView.adapter = adapter

        adapter.setActionModeReceiver(this as WeightAdapter.OnClickAction)

        //Recycler view elevation on scroll
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private val SCROLL_DIRECTION_UP = -1

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)){
                    toolbar.elevation = 20F
                }
                else{
                    toolbar.elevation = 0F
                }

            }
        })

        //Fab click
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
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
            var selectedItems = adapter.getSelectedItems()

            var realmSelectedItems = RealmList<Weight>()

            for (item in selectedItems) {
                var realmItem = realm.where<Weight>().equalTo("id", item.id).findFirst()
                realmSelectedItems.add(realmItem)
            }

            return when (item.itemId) {
                R.id.menu_delete ->
                {
                    mode.finish()

                    realm.executeTransaction(Realm.Transaction {
                        for (realmItem in realmSelectedItems) {
                            realmItem.deleteFromRealm()
                        }
                    })

                    adapter.clearSelected()

                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            adapter.clearSelected()
        }
    }

    override fun onClickAction() {
        val selected = adapter.getSelectedItems().count()
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(actionModeCallback)
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

