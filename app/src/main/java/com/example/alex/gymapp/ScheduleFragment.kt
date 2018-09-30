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
import android.app.Activity
import android.content.Intent

class ScheduleFragment : Fragment() , ExerciseAdapter.OnClickAction{

    lateinit var realm: Realm
    var actionMode: ActionMode? = null
    lateinit var adapter: ExerciseAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        realm = Realm.getDefaultInstance()

        setupRecyclerView()
        setupSpinner()

        //Fab click
        add_schedule_fab.setOnClickListener{
            val intent = Intent(context, EditExerciseActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            this.startActivityForResult(intent, 1)
        }
    }

    private fun setupSpinner(){
        val existingDays = getExistingDays()
        setSpinnerItems(existingDays)

        days_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>,
                                        selectedItemView: View, position: Int, id: Long) {

                val selectedExecutionDay = parentView.getItemAtPosition(position).toString()
                changeSchedule(selectedExecutionDay)
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun exerciseAdded(executionDay : String){
        val existingDays = getExistingDays()
        setSpinnerItems(existingDays)
        val itemId = existingDays.indexOf(executionDay)
        days_spinner.setSelection(itemId)
    }

    private fun setupRecyclerView(){

        //Setup RecyclerView
        recyclerView = view!!.findViewById(R.id.scheduleRW) as RecyclerView

        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = true
        lm.stackFromEnd = true

        recyclerView.layoutManager = lm

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
    }

    private fun setSpinnerItems(existingDays: ArrayList<String>){
        val dataAdapter = ArrayAdapter<String>(context,R.layout.spinner_toolbar_item, existingDays)

        dataAdapter.setDropDownViewResource(R.layout.spinner_toolbar_item_dropdown)

        days_spinner.adapter = dataAdapter
    }

    private fun changeSchedule(selectedExecutionDay:String){
        val exercisesOfDay = realm.where<Exercise>().equalTo("executionDay", selectedExecutionDay).findAll()

        adapter = ExerciseAdapter(exercisesOfDay, context!!,this)
        recyclerView.swapAdapter(adapter,false)
        adapter.setActionModeReceiver(this@ScheduleFragment as ExerciseAdapter.OnClickAction)
    }

    private fun getExistingDays () : ArrayList<String>{

        //Get exercises from realm
        val exercises = realm.where<Exercise>().findAll()

        var distinctDays : ArrayList<String> = arrayListOf()

        for (exercise in exercises) {
            val dayOfWeek = exercise.executionDay

            if(!distinctDays.contains(dayOfWeek)){
                distinctDays.add(dayOfWeek)
            }
        }
        return distinctDays
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

                    var itemCount = adapter.itemCount

                    if(itemCount == 0){

                        var existingDays = getExistingDays()

                        setSpinnerItems(existingDays)

                        changeSchedule(days_spinner.getItemAtPosition(0).toString())
                    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val executionDay = data!!.getStringExtra("executionDay")
                exerciseAdded(executionDay)
            }
            //Exercise deleted
            if (resultCode == Activity.RESULT_CANCELED) {
                var itemCount = adapter.itemCount

                if(itemCount == 0){

                    var existingDays = getExistingDays()

                    setSpinnerItems(existingDays)

                    changeSchedule(days_spinner.getItemAtPosition(0).toString())
                }
            }
        }
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
