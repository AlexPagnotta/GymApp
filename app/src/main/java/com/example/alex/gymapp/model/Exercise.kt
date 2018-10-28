package com.example.alex.gymapp.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.FieldPosition

open class Exercise(

        @PrimaryKey var id: Long = 0,

        var name: String = "",

        var weight: Double = 0.0,

        var minutesOfRest: Int = 0,

        var secondsOfRest: Int = 0,

        var series: RealmList<Series>,

        var executionDay : Int = 0,

        var position : Int = 0

) : RealmObject()

open class Series(

        @PrimaryKey var id: Long = 0,

        var repetitions : Int = 1,

        var weight: Double = 0.0

) : RealmObject()