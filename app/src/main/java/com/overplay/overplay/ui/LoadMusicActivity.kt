package com.overplay.overplay.ui

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.overplay.overplay.database.OverPlayViewModel
import android.os.Bundle
import com.overplay.overplay.R
import androidx.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.overplay.overplay.MainActivity
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.ActivityLoadMusicBinding
import com.overplay.overplay.ui.others.PlayMusicScreen
import com.overplay.overplay.util.FindMusic
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoadMusicActivity : AppCompatActivity() {
    var viewModel: OverPlayViewModel? = null

    lateinit var binding: ActivityLoadMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadMusicBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(OverPlayViewModel::class.java)


        if (!allPermissionGranted()) {
            requestNeededPermissions()
        }

        GlobalScope.launch(Dispatchers.IO) {

            retrieveMusic(this@LoadMusicActivity, viewModel)
        }



        binding.listenButton.setOnClickListener {
            val intent = Intent(this@LoadMusicActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerPreference() {
        val editor = getSharedPreferences("first_time", MODE_PRIVATE).edit()
        editor.putBoolean("first_time", false)
        editor.apply()
    }


    private var allRequiredPermissions = arrayOf(

        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,

        )

    private fun allPermissionGranted(): Boolean {
        for (permission in allRequiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestNeededPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                allRequiredPermissions,
                PackageManager.PERMISSION_GRANTED
            )
        }
    }

    fun retrieveMusic(
        activity: Activity,
        viewModel: OverPlayViewModel? = null,
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
        var cursor = activity.managedQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        var count = 0
        while (cursor.moveToNext()) {

            count++
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

            val folder = path.split("/")
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

            runOnUiThread {

                binding.text.text = "Scanning device for music in $path \n $count found"
            }

            output.add(music)
            viewModel?.insertSong(music)
            runOnUiThread {
                if (cursor.isLast) {

                    binding.listenButton.visibility = View.VISIBLE
                    binding.progress.visibility = View.GONE
                    binding.text.text = "Scanning for music Done"
                    registerPreference()


                }
            }

        }



        return output
    }


}