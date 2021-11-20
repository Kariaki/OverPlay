package com.overplay.overplay.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.entities.MusicItem

class OverPlayRepository(application: Application) {

    private val database: OverPlayDatabase = OverPlayDatabase.getDatabase(application)
    private val dbDao: OverPlayDao = database.musicDao()


    fun allSongs(): LiveData<MutableList<MusicItem>> = dbDao.retrieveMusic()
    suspend fun insertSong(music: MusicItem) {
        dbDao.insertSong(music)
    }

    suspend fun updateSong(music: MusicItem) {
        dbDao.updateSong(music)
    }

    suspend fun deleteSong(music: MusicItem) {
        dbDao.deleteSong(music)
    }

    suspend fun insertPlay(music: CurrentlyPlayingMusic) {
        dbDao.insertPlay(music)
    }

    fun getCurrentlyPlaying(): LiveData<MutableList<CurrentlyPlayingMusic>> =
        dbDao.currentlyPlaying()

    fun allArtist(artist: String): LiveData<MutableList<MusicItem>> =
        dbDao.retrieveArtistSongs(artist)

    fun allAlbum(album: String): LiveData<MutableList<MusicItem>> = dbDao.retrieveAlbumSongs(album)

    fun retrieveAlbums(): LiveData<MutableList<MusicItem>> = dbDao.retrieveAlbums()

    fun likedSongs(): LiveData<MutableList<MusicItem>> = dbDao.retrieveLikedSongs()
    fun getRecentlyPlayed(): LiveData<MutableList<MusicItem>> = dbDao.getRecentlyPlayed()

    fun retrieveFolders(): LiveData<MutableList<MusicItem>> = dbDao.retrieveFolders()

    fun retrieveArtist(): LiveData<MutableList<MusicItem>> = dbDao.retrieveArtist()

    fun retrieveArtistSongs(artist:String):LiveData<MutableList<MusicItem>> =dbDao.retrieveArtistSongs(artist)

    fun getFolderSongs(folder: String): LiveData<Int> =
        dbDao.getFolderSongs(folder)

    fun retrieveFolderSongs(folder: String): LiveData<MutableList<MusicItem>> =
        dbDao.retrieveFolderSongs(folder)

    fun searchSongs(query: String): LiveData<MutableList<MusicItem>> =
        dbDao.searchSongs(query)


}