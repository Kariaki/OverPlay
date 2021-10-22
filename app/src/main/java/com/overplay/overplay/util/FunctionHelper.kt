package com.overplay.overplay.util

object FunctionHelper {

    //process artist name to remove angle brackets
    fun processArtist(artistName:String):String= if(artistName.startsWith('<') && artistName.endsWith('>'))artistName.subSequence(1,artistName.length-1).toString()else artistName

}