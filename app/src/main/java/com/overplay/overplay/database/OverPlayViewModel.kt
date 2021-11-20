package com.overplay.overplay.database

import android.app.Application
import androidx.lifecycle.*
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.entities.MusicItem
import kotlinx.coroutines.launch

class OverPlayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OverPlayRepository
    val allMusic: LiveData<MutableList<MusicItem>>
    var allAlbums: LiveData<MutableList<MusicItem>>
    var currentlyPlayingMusic: LiveData<MutableList<CurrentlyPlayingMusic>>
    var recentlyPlayed: LiveData<MutableList<MusicItem>>
    var folders: LiveData<MutableList<MusicItem>>
    var likedSongs: LiveData<MutableList<MusicItem>>

    init {

        repository = OverPlayRepository(application)
        allMusic = repository.allSongs()
        allAlbums = repository.retrieveAlbums()
        currentlyPlayingMusic = repository.getCurrentlyPlaying()
        recentlyPlayed = repository.getRecentlyPlayed()
        folders = repository.retrieveFolders()
        likedSongs = repository.likedSongs()
    }

    fun insertSong(musicItem: MusicItem) {

        viewModelScope.launch {
            repository.insertSong(musicItem)
        }

    }

    fun updateSong(musicItem: MusicItem) {

        viewModelScope.launch {
            repository.updateSong(musicItem)
        }

    }

    fun insertPlay(musicItem: CurrentlyPlayingMusic) {

        viewModelScope.launch {
            repository.insertPlay(musicItem)
        }

    }

    fun deleteSong(musicItem: MusicItem) {

        viewModelScope.launch {
            repository.deleteSong(musicItem)
        }


    }

    fun getFolderCount(folder: String): LiveData<Int> = repository.getFolderSongs(folder)


    fun albumSongs(album: String): LiveData<MutableList<MusicItem>> = repository.allAlbum(album)

    fun artistSongs(artist:String):LiveData<MutableList<MusicItem>> = repository.retrieveArtistSongs(artist)
    fun folderSongs(folder: String): LiveData<MutableList<MusicItem>> =
        repository.retrieveFolderSongs(folder)

    fun searchSongs(query: String): LiveData<MutableList<MusicItem>> =
        repository.searchSongs(query)

}