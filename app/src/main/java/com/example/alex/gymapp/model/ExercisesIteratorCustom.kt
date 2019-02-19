package com.example.alex.gymapp.model

class ExercisesIteratorCustom internal constructor(private val exercises: List<Exercise>)
{
    private var counter:Int = 0

   fun next() : Exercise?{

       if(counter>exercises.count()) return null

       val exercise = exercises[counter]
       counter++
       return exercise
   }

    fun previous() : Exercise?{
        counter--

        if(counter == 0) return null

        val exercise = exercises[counter]
        return exercise
    }

}