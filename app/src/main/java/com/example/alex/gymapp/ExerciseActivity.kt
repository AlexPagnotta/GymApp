package com.example.alex.gymapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.alex.gymapp.model.Exercise
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.fragment_weights.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.app.Activity

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

            //builder.setTitle("Delete ");
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
        weightTw.text = String.format("%1$,.2f Kg", exercise.weight)
        restTw.text =String.format("%dM %dS", exercise.minutesOfRest, exercise.secondsOfRest)
        seriesTw.text =String.format("%dx%d ", exercise.series,exercise.repetitions )
        executionDayTw.text = exercise.executionDay
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                reloadUI()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
