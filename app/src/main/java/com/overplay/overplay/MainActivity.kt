package com.overplay.overplay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.DependencyInjection.Car
import com.overplay.overplay.database.PlayViewModel
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.entities.SavedSharedPreference
import com.overplay.overplay.databinding.ActivityMainBinding
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.screens.PlayMusicScreen
import com.overplay.overplay.screens.SongsScreen
import com.overplay.overplay.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var car: Car
    private lateinit var navController: NavController

    private lateinit var viewModel: OverPlayViewModel


    private var allRequiredPermissions = arrayOf(

        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,

        )
    lateinit var currentMusic: BackgroundMusicService
    lateinit var configuration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    lateinit var playViewModel: PlayViewModel
    var currentlyPlayingMusic: CurrentlyPlayingMusic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // setTheme(R.style.Theme.overplay)

      //  window.setBackgroundDrawableResource(R.drawable.splash_design_light)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Intent(this, BackgroundMusicService::class.java).also { intent ->
            bindService(intent, Connections(connectionExecutor), Context.BIND_AUTO_CREATE)
        }
        setContentView(binding.root)

        connection = Connections(connectionExecutor)
        playViewModel = ViewModelProvider(this).get(PlayViewModel::class.java)


        configuration = AppBarConfiguration(
            setOf(R.id.overPlayHome, R.id.searchScreen, R.id.libraryScreen)
        )
        setSupportActionBar(binding.toolbar)


        val hostFragment = supportFragmentManager.findFragmentById(R.id.navController)
        navController = hostFragment?.findNavController()!!

        setupActionBarWithNavController(navController, configuration)


        if (!allPermissionGranted()) {
            requestNeededPermissions()
        }

        viewModel = ViewModelProvider(this).get(OverPlayViewModel(application!!)::class.java)

        viewModel.currentlyPlayingMusic.observe(this,  {

            if (it.isNotEmpty()) {

                val artistName=it[0].artist
                currentlyPlayingMusic = it[0]
                binding.playingMusicHolder.visibility = View.VISIBLE
                binding.songName.text = it[0].tittle
                binding.artistName.text = FunctionHelper.processArtist(artistName!!)
                binding.likeButton.isChecked=currentlyPlayingMusic?.liked!!
                Glide.with(this).load(it[0].albumArt)
                    .placeholder(R.drawable.music_place_holder)
                    .into(binding.playingMusicAlbumArt)

            }else{

                binding.playingMusicHolder.visibility = View.GONE
            }

        })
        binding.likeButton.setOnCheckedChangeListener { compoundButton, b ->
            if(currentlyPlayingMusic!=null) {
                val musicItem = SongsScreen.convertBackToMusicItem(currentlyPlayingMusic!!)
                musicItem.liked = b
                viewModel.updateSong(musicItem)
            }
        }

        fillInitialQueue()

        GlobalScope.launch(Dispatchers.Default) {

           // FindMusic.retrieveMusic(this@MainActivity, viewModel)

        }


        binding.bottomNav.setupWithNavController(navController)

        binding.playingMusicHolder.setOnClickListener {

            var intent = Intent(this, PlayMusicScreen::class.java)
            startActivity(intent)
        }


        binding.musicStateIcon.setOnCheckedChangeListener { compoundButton, isChecked ->
            playViewModel.musicState.value = isChecked
            if (isChecked) {

                currentMusic.toggleMusic(true)
            } else {

                currentMusic.toggleMusic(false)
            }

        }


    }

    fun observePlayState() {
        playViewModel.musicState.observe(this, Observer {
            binding.musicStateIcon.isChecked = it
            //currentMusic.toggleMusic(it)

        })
    }

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

    override fun onResume() {
        super.onResume()

        observePlayState()

    }

    override fun onStart() {
        super.onStart()
        Intent(this, BackgroundMusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    lateinit var connection: Connections
    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    var connectionExecutor = object : ConnectionExecutor {
        override fun execute(binderService: BackgroundMusicService) {
            currentMusic = binderService
            currentMusic.imagePlaceHolder = R.drawable.music_place_holder
            currentMusic.playButton = R.drawable.play_alt
            currentMusic.pauseButton = R.drawable.pause_alt
            binding.musicPlayProgress.progress = 0
            timerCounter()
            binding.musicStateIcon.isChecked = currentMusic.isPlaying()
            currentMusic.bindViewModel(viewModel)
            currentMusic.activity = this@MainActivity
            observePlayState()

            if (currentMusic.musicQueue.isEmpty()) {

                if (initialQueue.isNotEmpty()) {
                    currentMusic.playMusic(
                        initialQueue,
                        savedData.position,
                        viewModel,
                        false,
                        savedData.type,
                        this@MainActivity,
                        savedData.name,
                        false
                    )
                }
            }

        }
    }

    var handler = Handler()
    var initialQueue: MutableList<MusicItem> = mutableListOf()
    lateinit var savedData: SavedSharedPreference

    fun fillInitialQueue() {
        val name = MusicQueueHelper.getSavedDate(this@MainActivity)
        savedData = name
        if (name.type == Constants.ALL_SONGS) {
            viewModel.allMusic.observe(this, {
                initialQueue = it
            })
        } else if (name.type == Constants.ALBUM_SONGS) {
            viewModel.albumSongs(name.name).observe(this, {
                initialQueue = it
            })
        } else if (name.type == Constants.FOLDER_SONGS) {
            viewModel.folderSongs(name.name).observe(this, {
                initialQueue = it
            })
        } else if (name.type == Constants.RECENTLY_PLAYED_SONGS) {
            viewModel.recentlyPlayed.observe(this, {
                initialQueue = it
            })
        }else if(name.type== Constants.LIKED_SONGS){
            viewModel.likedSongs.observe(this,{
                initialQueue=it
            })
        }else if(name.type== Constants.SEARCH_SONGS){

            viewModel.searchSongs(name.name).observe(this, {
                initialQueue = it
            })

        }else{
            initialQueue.clear() //set initial queue to empty
        }

    }

    private fun timerCounter() {

        updateSeekbar()
        handler.postDelayed(runnable, 1000)
    }

    // @RequiresApi(api = Build.VERSION_CODES.N)


    lateinit var runnable: Runnable

    fun setProgress() {

        val current: Int = currentMusic.mediaPlay.currentPosition
        val progress: Int = current * 100 / currentMusic.mediaPlay.duration
        binding.musicPlayProgress.progress = progress

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateSeekbar() {

        binding.musicStateIcon.isChecked = currentMusic.mediaPlay.isPlaying

        setProgress()

        runnable = Runnable {
            updateSeekbar()
        }
        handler.postDelayed(runnable, 1000)
    }

}