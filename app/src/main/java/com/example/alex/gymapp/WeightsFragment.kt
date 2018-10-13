package com.example.alex.gymapp

import android.content.DialogInterface
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
import io.realm.RealmList
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import io.realm.RealmResults
import io.realm.Sort


class WeightsFragment : Fragment(), WeightAdapter.OnClickAction, DialogInterface.OnDismissListener{

    lateinit var realm: Realm
    var actionMode: ActionMode? = null
    lateinit var adapter: WeightAdapter

    lateinit var weights: RealmResults<Weight>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weights, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        realm = Realm.getDefaultInstance()

        weights = realm.where<Weight>().findAll().sort("dateOfWeight",Sort.DESCENDING)

        setupRecyclerView()

        reloadUi()

        //Recycler view elevation on scroll
        weightsRW.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
            val ft = childFragmentManager.beginTransaction()
            val prev = childFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = EditWeightFragmentDialog()
            dialogFragment.show(ft, "dialog")
        }
    }

    private fun setupRecyclerView(){
        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        weightsRW.layoutManager = lm

        adapter = WeightAdapter(weights, context!!)
        weightsRW.adapter = adapter

        adapter.setActionModeReceiver(this as WeightAdapter.OnClickAction)
    }

    public fun reloadUi(){
        if(weightsRW.adapter!!.itemCount==0){
            toolbar.visibility = View.GONE
            weightsRW.visibility = View.GONE
            recyclerEmptyTW.visibility = View.VISIBLE
            return
        }
        else
        {
            toolbar.visibility = View.VISIBLE
            weightsRW.visibility = View.VISIBLE
            recyclerEmptyTW.visibility = View.GONE
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

                    reloadUi()

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

    override fun onDismiss(dialog: DialogInterface?) {
        reloadUi()
    }

    companion object {
        fun newInstance(): WeightsFragment = WeightsFragment()
    }
}

