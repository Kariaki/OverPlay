package com.overplay.overplay.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.overplay.overplay.notification.BackgroundMusicService

import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.entities.SavedSharedPreference

class MusicQueueHelper {

    companion object {


        fun savePlayQueue(
            music: MusicItem,
            activity: AppCompatActivity,
            type: Int,
            name: String = "",
            position: Int
        ) {
            val sharedPreferences =
                activity.getSharedPreferences("saved music", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("type", type)
            editor.putInt("position", position)
            when (type) {
                Constants.ALL_SONGS ->
                    editor.putString("name", " ")
                Constants.ALBUM_SONGS ->
                    editor.putString("name", music.album)
                Constants.PLAYLIST_SONGS ->
                    editor.putString("name", name)
                Constants.FOLDER_SONGS ->
                    editor.putString("name", music.folder)
                Constants.LIKED_SONGS ->

                    editor.putString("name", "liked")

            }
            editor.apply()

        }

        fun getSavedDate(activity: AppCompatActivity): SavedSharedPreference {
            val sharedPreferences =
                activity.getSharedPreferences("saved music", Context.MODE_PRIVATE)

            return SavedSharedPreference(
                sharedPreferences.getString("name", " ")!!,
                sharedPreferences.getInt("type", Constants.ALL_SONGS),
                sharedPreferences.getInt("position", 0)
            )
        }

        fun getSavedDate(activity: BackgroundMusicService): SavedSharedPreference {
            val sharedPreferences =
                activity.getSharedPreferences("saved music", Context.MODE_PRIVATE)

            return SavedSharedPreference(
                sharedPreferences.getString("name", " ")!!,
                sharedPreferences.getInt("type", Constants.ALL_SONGS),
                sharedPreferences.getInt("position", 0)
            )
        }

    }

}