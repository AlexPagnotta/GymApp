package com.example.alex.gymapp.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Exercise(

        @PrimaryKey var id: Long = 0,

        var name: String = "",

        var weight: Double = 0.0,

        var minutesOfRest: Int = 0,

        var secondsOfRest: Int = 0,

        var series: Int = 1,

        var repetitions : Int = 1,

        var executionDay : String = ""

) : RealmObject()