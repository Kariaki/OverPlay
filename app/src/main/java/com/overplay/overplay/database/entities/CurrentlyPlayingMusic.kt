package com.overplay.overplay.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.overplay.overplay.Adapter.SuperEntity
import com.overplay.overplay.util.Constants

@Entity(tableName = "currentlyPlaying")
open class CurrentlyPlayingMusic(
    var path: String? = null,
    var tittle: String? = null,
    @ColumnInfo(name = "artist") var artist: String? = null,
    var displayName: String? = null,
    var duration: String? = null,
    @ColumnInfo(name = "album")
    var album:String?=null,
    var genre:String?=null,
    var albumArt:String?=null,
    @PrimaryKey
    val id: Int? = null,
    val musicId:Int?=null,
    var queueType:Int= Constants.ALL_SONGS,
    var playTime:Long=0,
    var playCount:Int=0,
    var folder:String?=null,
    var position:Int?=null,
    var liked:Boolean=false
):SuperEntity()