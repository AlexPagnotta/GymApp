package com.example.alex.gymapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ScheduleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startScheduleService(){
    }

    private fun stopScheduleService() {

    }
}