package com.overplay.overplay.notification

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class LoadMusicService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    inner class MyBinder : Binder() {
        fun getService(): LoadMusicService = this@LoadMusicService
    }
}