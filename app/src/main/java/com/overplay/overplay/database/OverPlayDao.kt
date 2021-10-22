package com.overplay.overplay.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.entities.MusicItem

@Dao
interface OverPlayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(music: MusicItem)

    @Update
    suspend fun updateSong(music: MusicItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlay(music: CurrentlyPlayingMusic)

    @Update
    suspend fun updatePlay(music: CurrentlyPlayingMusic)

    @Delete
    suspend fun deleteSong(music: MusicItem)

    @Query("select * from songs order by tittle asc")
    fun retrieveMusic(): LiveData<MutableList<MusicItem>>

    @Query("select * from songs where artist like :artist")
    fun retrieveArtistSongs(artist: String): LiveData<MutableList<MusicItem>>

    @Query("select * from songs where album like:album")
    fun retrieveAlbumSongs(album: String): LiveData<MutableList<MusicItem>>

    @Query("select * from songs group by album order by playTime desc limit 30 ")
    fun retrieveAlbums(): LiveData<MutableList<MusicItem>>

    @Query("select * from songs group by folder order by folder asc ")
    fun retrieveFolders(): LiveData<MutableList<MusicItem>>

    @Query("select * from songs order by playTime desc limit 6")
    fun getRecentlyPlayed(): LiveData<MutableList<MusicItem>>

    @Query("select * from songs group by artist")
    fun retrieveArtist(): LiveData<MutableList<MusicItem>>

    @Query("select * from currentlyPlaying")
    fun currentlyPlaying(): LiveData<MutableList<CurrentlyPlayingMusic>>


    @Query("select count(*) from songs where folder like :folder")
    fun getFolderSongs(folder: String): LiveData<Int>

    @Query("select * from songs where folder like :folder")
    fun retrieveFolderSongs(folder: String): LiveData<MutableList<MusicItem>>

    @Query("select * from songs where tittle like  :query or artist like  :query  or folder like  :query or displayName like:query or album like:query ")
    fun searchSongs(query: String): LiveData<MutableList<MusicItem>>


    @Query("select * from songs where liked  ")
    fun retrieveLikedSongs(): LiveData<MutableList<MusicItem>>


}