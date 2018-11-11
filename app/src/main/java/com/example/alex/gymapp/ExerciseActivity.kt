package com.example.alex.gymapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_exercise.*
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import com.example.alex.gymapp.adapters.SeriesAdapter
import com.example.alex.gymapp.utilities.Utilities

class ExerciseActivity : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        //Get Exercise
        val exerciseId = intent.getLongExtra("exerciseId",0)
        realm = Realm.getDefaultInstance()
        exercise = realm.where<Exercise>().equalTo("id",exerciseId).findFirst()!!
        //Reload UI With Data
        reloadUI()

        backBtn.setOnClickListener{
            val returnIntent = Intent()
            returnIntent.putExtra("executionDay",exercise.executionDay)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        editBtn.setOnClickListener{
            val intent = Intent(this, EditExerciseActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("exerciseId", exercise.id)
            this.startActivityForResult(intent, 3)
        }

        deleteBtn.setOnClickListener{
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Delete this exercise?")
                    .setCancelable(false)
                    .setPositiveButton("CONFIRM"
                    ) { _, _ ->

                        realm.executeTransaction(Realm.Transaction {
                            exercise.deleteFromRealm();
                        })

                        val returnIntent = Intent()
                        setResult(Activity.RESULT_CANCELED, returnIntent)
                        finish()
                    }
                    .setNegativeButton("CANCEL") { _, _ ->}

            builder.show()
        }
    }

    private fun reloadUI(){
        //Show exercise data
        var name = exercise.name
        if(name.isEmpty()){
            name = "No Exercise Name"
        }
        titleTw.text = name
        restTw.text =String.format("%dM %dS", exercise.minutesOfRest, exercise.secondsOfRest)
        val executionDayString = Utilities.getExecutionDayString(this, exercise.executionDay)
        executionDayTw.text = executionDayString
        setupSeriesRecyclerView()
    }

    private fun setupSeriesRecyclerView() {
        //Load series into recycler view
        val lm = LinearLayoutManager(this)
        seriesRW.layoutManager = lm
        val adapter = SeriesAdapter(exercise.series, this)
        //Remove scroll
        seriesRW.isNestedScrollingEnabled = false
        //Set adapter
        seriesRW.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                //Reload ui when exercise has been edited
                reloadUI()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
