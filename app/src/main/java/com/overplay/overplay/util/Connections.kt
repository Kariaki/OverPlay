package com.overplay.overplay.util

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.overplay.overplay.notification.BackgroundMusicService

open class Connections(val connectionExecutor: ConnectionExecutor):ServiceConnection{

    lateinit var myservice: BackgroundMusicService

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        var binder = p1 as BackgroundMusicService.MyBinder
        myservice = binder.getService()
        connectionExecutor.execute(myservice)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {

    }


}