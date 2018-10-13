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
import android.support.v7.widget.helper.ItemTouchHelper
import com.example.alex.gymapp.helpers.SimpleItemTouchHelperCallback
import android.view.LayoutInflater

class ScheduleFragment : Fragment() , ExerciseAdapter.OnClickAction, ExerciseAdapter.OnStartDragListener {

    lateinit var realm: Realm
    var actionMode: ActionMode? = null
    lateinit var adapter: ExerciseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemTouchHelper : ItemTouchHelper
    var selectedExecutionDay = 0

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
        reloadSchedule()

        //Fab click
        add_schedule_fab.setOnClickListener{
            val intent = Intent(context, EditExerciseActivity::class.java)
            this.startActivityForResult(intent, 1)
        }
    }

    private fun setupRecyclerView(){

        //Setup RecyclerView
        recyclerView = view!!.findViewById(R.id.scheduleRW) as RecyclerView
        var lm = LinearLayoutManager(context!!)

        //Reverse the recycler view
        lm.reverseLayout = false
        lm.stackFromEnd = false

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

    private fun setupSpinner(){
        //Get all the distinct execution days, and put them on the spinner
        var existingDays = getExistingDays()
        setSpinnerItems(existingDays)

        days_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>,
                                        selectedItemView: View, position: Int, id: Long) {
                //Update existing days
                existingDays = getExistingDays()
                //Get selected day
                selectedExecutionDay = existingDays[position]
                //Change the schedule
                reloadSchedule()
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun setSpinnerItems(existingDays: ArrayList<Int>){
        //Days to show on spinner in strings
        val daysStrings : ArrayList<String> = arrayListOf()
        //Array of string, containing the days
        val weekDaysArray = resources.getStringArray(R.array.days_of_week)
        //Get days strings
        for (day in existingDays) {
            daysStrings.add(weekDaysArray[day])
        }
        //Set adapter
        val dataAdapter = ArrayAdapter<String>(context!!,R.layout.spinner_toolbar_item, daysStrings)
        dataAdapter.setDropDownViewResource(R.layout.spinner_toolbar_item_dropdown)
        days_spinner.adapter = dataAdapter
    }

    private fun reloadSchedule(){
        val realmExercise = realm.where<Exercise>().findAll()
        //If there are no exercise left at all, show different ui
        if(realmExercise.count()==0){
            toolbar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            recyclerEmptyTW.visibility = View.VISIBLE
            return
        }
        else
        {
            toolbar.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            recyclerEmptyTW.visibility = View.GONE
        }

        //Get exercises of the selected day
        val exercisesOfDayRealm = realm.where<Exercise>().equalTo("executionDay", selectedExecutionDay).findAll().sort("position")
        val exercisesOfDay = realm.copyFromRealm(exercisesOfDayRealm)

        //Set them to recycler view
        adapter = ExerciseAdapter(exercisesOfDay as ArrayList<Exercise>, context!!,this)
        adapter.setDragStartListener(this)
        recyclerView.swapAdapter(adapter,false)
        adapter.setActionModeReceiver(this@ScheduleFragment as ExerciseAdapter.OnClickAction)

        //Set adapter for drag drop
        val callback = SimpleItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun getExistingDays () : ArrayList<Int>{
        //Get exercises from realm
        val exercises = realm.where<Exercise>().findAll()

        //Get distinct days
        var distinctDays : ArrayList<Int> = arrayListOf()
        for (exercise in exercises) {
            val dayOfWeek = exercise.executionDay
            if(!distinctDays.contains(dayOfWeek)){
                distinctDays.add(dayOfWeek)
            }
        }

        //Reorder
        distinctDays.sort()
        return distinctDays
    }

    private fun exerciseAdded(executionDay : Int){
        //Set correct schedule when an exercise is added
        //Update spinner days
        val existingDays = getExistingDays()
        setSpinnerItems(existingDays)
        //Get index from existing days
        val dayIndex = existingDays.indexOf(executionDay)
        //Set spinner position
        days_spinner.setSelection(dayIndex)
        //Reload Schedule
        reloadSchedule()
    }

    private fun exerciseDeleted(){
        val exercises= realm.where<Exercise>().equalTo("executionDay", selectedExecutionDay).findAll()
        val existingDays = getExistingDays()
        //If there are no exercise left in the selected day, change schedule to first
        if(exercises.count() == 0 && existingDays.count() != 0){
            setSpinnerItems(existingDays)
            selectedExecutionDay = 0
        }

        reloadSchedule()
    }

    //Drag Drop
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    //Action mode callback
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.weight_multi_selection, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            //Selected Items
            val selectedItems = adapter.getSelectedItems()
            //List to store selected item of realm
            val realmSelectedItems = RealmList<Exercise>()

            for (selectedItem in selectedItems) {
                val realmItem = realm.where<Exercise>().equalTo("id", selectedItem.id).findFirst()
                realmSelectedItems.add(realmItem)
            }

            return when (item.itemId) {
                R.id.menu_delete ->
                {
                    //When item is deleted
                    //End Action mode
                    mode.finish()
                    //Delete Selected items
                    realm.executeTransaction(Realm.Transaction {
                        for (realmItem in realmSelectedItems) {
                            realmItem.deleteFromRealm()
                        }
                    })
                    //Clear Selected
                    adapter.clearSelected()

                    exerciseDeleted()
                    true
                } else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            adapter.clearSelected()
        }
    }

    //Select days on action mode
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
            //Exercise Added
            if (resultCode == Activity.RESULT_OK) {
                //Get execution day of the added exercise
                val executionDay = data!!.getIntExtra("executionDay",0)
                exerciseAdded(executionDay)
            }
            //Exercise Deleted
            if (resultCode == Activity.RESULT_CANCELED) {
                exerciseDeleted()
            }
        }
    }

    companion object {
        fun newInstance(): ScheduleFragment = ScheduleFragment()
    }
}
