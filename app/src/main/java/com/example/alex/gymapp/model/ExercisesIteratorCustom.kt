package com.example.alex.gymapp.model

class ExercisesIteratorCustom internal constructor(private val exercises: List<Exercise>)
{
    private var counter:Int = 0

    fun current() : Exercise{
       return exercises[counter]
    }

   fun next() : Boolean {
       if(counter>=exercises.count() - 1) return true
       counter++
       return false
   }

    fun previous() : Boolean{

        if(counter == 0) return true
        counter--
        return false
    }

}