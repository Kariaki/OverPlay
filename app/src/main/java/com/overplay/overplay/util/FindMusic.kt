package com.overplay.overplay.util

import android.app.Activity
import android.content.ContentUris
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem


object FindMusic {


    fun retrieveMusic(
        activity: Activity,
        viewModel: OverPlayViewModel? = null
    ): MutableList<MusicItem> {

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        var output: MutableList<MusicItem> = mutableListOf()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,

            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val genresProjection = arrayOf(
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
        )

        var cursor = activity.managedQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        while (cursor.moveToNext()) {


            val artist = cursor.getString(1)
            val tittle = cursor.getString(2)
            val path = cursor.getString(3)
            val duration = cursor.getString(5)
            val displayName = cursor.getString(4)
            val albumArtId = cursor.getLong(7)
            var musicId = cursor.getInt(0)

            val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(0)
            )

            var metadataRetriever = MediaMetadataRetriever()
            metadataRetriever.setDataSource(activity.applicationContext, trackUri)

            val genre: String? =
                metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

            var albumArt = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumArtId
            )

            var folder = path.split("/")
            val music = MusicItem(

                tittle = tittle,
                artist = artist,
                duration = duration,
                displayName = displayName,
                path = path,
                album = cursor.getString(6),
                albumArt = albumArt.toString(),
                genre = genre,
                id = musicId,
                folder = folder[folder.size - 2]
            )


            output.add(music)
            viewModel?.insertSong(music)
        }
        return output
    }


}


