package com.overplay.overplay.database

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayViewModel: ViewModel() {

    val musicState= MutableLiveData<Boolean> ()
    val musicPosition=MutableLiveData<Int>()
    val queueType=MutableLiveData<Int>()

    fun setPlayValue(state:Boolean){
        musicState.value=state
    }
    fun setPosition(position:Int){
        musicPosition.value=position
    }
    fun setQueueType(type:Int){
        queueType.value=type
    }
}