package com.overplay.overplay.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.overplay.overplay.Adapter.SuperEntity

@Entity(tableName = "songs")
open class MusicItem(
    var path: String? = null,
    var tittle: String? = null,
    @ColumnInfo(name = "artist") var artist: String? = null,
    var displayName: String? = null,
    var duration: String? = null,
    @ColumnInfo(name = "album")
    var album: String? = null,
    var genre: String? = null,
    var albumArt: String? = null,
    @PrimaryKey
    val id: Int? = null,
    var playTime: Long = 0,
    var playCount: Int = 0,
    var folder: String? = null,
    var liked:Boolean=false

) : SuperEntity(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(tittle)
        parcel.writeString(artist)
        parcel.writeString(displayName)
        parcel.writeString(duration)
        parcel.writeString(album)
        parcel.writeString(genre)
        parcel.writeValue(albumArt)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicItem> {
        override fun createFromParcel(parcel: Parcel): MusicItem {
            return MusicItem(parcel)
        }

        override fun newArray(size: Int): Array<MusicItem?> {
            return arrayOfNulls(size)

        }
    }


}
