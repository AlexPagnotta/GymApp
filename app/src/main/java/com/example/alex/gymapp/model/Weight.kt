package com.example.alex.gymapp.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Weight(
        @PrimaryKey var id: Long = 0,

        var dateOfWeight: Date =  Date(),

        var weight: Double = 0.0

) : RealmObject() {
        // The Kotlin compiler generates standard getters and setters.
        // Realm will overload them and code inside them is ignored.
        // So if you prefer you can also just have empty abstract methods.
}