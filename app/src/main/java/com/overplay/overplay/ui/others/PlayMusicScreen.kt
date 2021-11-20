package com.overplay.overplay.ui.others

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.R
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.databinding.PlayMusicScreenBinding
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.util.TimeFactor
import jp.wasabeef.glide.transformations.BlurTransformation

class PlayMusicScreen : AppCompatActivity() {

    // val args:PlayMusicScreenArgs by navArgs()
    private lateinit var viewModel: OverPlayViewModel

    lateinit var currentlyPlayingMusic: CurrentlyPlayingMusic
    lateinit var binding: PlayMusicScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, BackgroundMusicService::class.java).also { intent ->
            bindService(intent, Connections(connectionExecutor), Context.BIND_AUTO_CREATE)
        }
        binding = PlayMusicScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(this).get(OverPlayViewModel(application!!)::class.java)

        binding.musicProgress.max = 100
        viewModel.currentlyPlayingMusic.observe(this, Observer {
            currentlyPlayingMusic = it[0]
            binding.musicTittle.text = currentlyPlayingMusic.tittle
            binding.artistName.text = currentlyPlayingMusic.artist

            Glide.with(this).load(currentlyPlayingMusic.albumArt)
                .placeholder(R.drawable.music_place_holder)
                .transform(BlurTransformation(100, 2))
                .into(binding.albumArtBackground)
            Glide.with(this).load(currentlyPlayingMusic.albumArt)
                .placeholder(R.drawable.music_place_holder)
                .into(binding.albumArtImage)

            binding.backButton.setOnClickListener {
                onBackPressed()
            }
        })

        binding.musicControl.setOnCheckedChangeListener { compoundButton, isChecked ->

            currentMusic.toggleMusic(isChecked)

        }

        binding.previousButton.setOnClickListener {
            currentMusic.playPrevious()
            //  updatePlayingMusic()
        }
        binding.nextButton.setOnClickListener {
            currentMusic.playNext()
            // updatePlayingMusic()
        }

        binding.repeatState.setOnCheckedChangeListener { compoundButton, b ->

            currentMusic.loop = b
        }


        binding.musicProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {


            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val seekPosition =
                    (currentMusic.mediaPlay.duration * p0?.progress!! / 100)
                currentMusic.seekTo(seekPosition)
            }
        })

    }


    override fun onStart() {
        super.onStart()
        Intent(this, BackgroundMusicService::class.java).also { intent ->
            bindService(intent, Connections(connectionExecutor), Context.BIND_AUTO_CREATE)
        }

    }

    lateinit var currentMusic: BackgroundMusicService


    var connectionExecutor = object : ConnectionExecutor {
        override fun execute(binderService: BackgroundMusicService) {
            currentMusic = binderService

            binding.musicControl.isChecked = currentMusic.isPlaying()
            binding.repeatState.isChecked = currentMusic.loop

            binding.playPosition.text =
                TimeFactor.convertMillieToHMmSs(currentMusic.mediaPlay.duration.toLong())
            timerCounter()
            currentMusic.bindViewModel(viewModel)
        }
    }

    var handler = Handler()

    private fun timerCounter() {

        updateSeekbar()
        handler.postDelayed(runnable, 1000)
    }

    // @RequiresApi(api = Build.VERSION_CODES.N)


    lateinit var runnable: Runnable

    fun setProgress() {

        val current: Int = currentMusic.mediaPlay.currentPosition
        val progress: Int = current * 100 / currentMusic.mediaPlay.duration
        binding.musicProgress.progress = progress

    }

    fun updateSeekbar() {
        if(currentMusic!=null) {
            val position = currentMusic.mediaPlay.currentPosition
            binding.musicControl.isChecked = currentMusic.mediaPlay.isPlaying

            setProgress()
            runOnUiThread {
                binding.duration.text = TimeFactor.convertMillieToHMmSs(position.toLong())
            }
            runnable = Runnable {
                updateSeekbar()
            }
            handler.postDelayed(runnable, 1000)
        }
    }

}