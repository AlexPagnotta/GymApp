package com.example.alex.gymapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.alex.gymapp.adapters.ExerciseAdapter
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : Fragment() , ExerciseAdapter.OnClickAction {

    lateinit var realm: Realm
    var actionMode: ActionMode? = null
    lateinit var adapter: ExerciseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        realm = Realm.getDefaultInstance()

        val exercises = realm.where<Exercise>().findAll()

        var recyclerView = view.findViewById(R.id.scheduleRW) as RecyclerView

        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        recyclerView.layoutManager = lm

        adapter = ExerciseAdapter(exercises, context!!)

        recyclerView.adapter = adapter

        adapter.setActionModeReceiver(this as ExerciseAdapter.OnClickAction)

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

        //Spinner
        var distinctDays : ArrayList<String> = arrayListOf()

        for (exercise in exercises) {
            val dayOfWeek = exercise.executionDay

            if(!distinctDays.contains(dayOfWeek)){
                distinctDays.add(dayOfWeek)
            }
        }

        if(distinctDays.count() > 0){
            var selectedExecutionDay = distinctDays[0]

            val dataAdapter = ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,distinctDays)

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            days_spinner.adapter = dataAdapter

            days_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>,
                                            selectedItemView: View, position: Int, id: Long) {
                    selectedExecutionDay = parentView.getItemAtPosition(position).toString()
                    val exercisesOfDay = realm.where<Exercise>().equalTo("executionDay", selectedExecutionDay).findAll()

                    recyclerView.swapAdapter(ExerciseAdapter(exercisesOfDay, context!!),false)
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                }
            }
        }
        else{
            //TODO Set visibility false
        }

        //Fab click
        add_schedule_fab.setOnClickListener{
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = AddExerciseFragmentDialog()
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

            var realmSelectedItems = RealmList<Exercise>()

            for (item in selectedItems) {
                var realmItem = realm.where<Exercise>().equalTo("id", item.id).findFirst()
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
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
