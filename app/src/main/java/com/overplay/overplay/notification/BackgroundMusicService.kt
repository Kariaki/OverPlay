package com.overplay.overplay.notification

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.screens.SongsScreen
import android.app.NotificationManager

import android.app.Notification

import android.app.NotificationChannel
import android.content.Context

import android.os.Build
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.overplay.overplay.util.Constants
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.screens.PlayMusicScreen
import com.overplay.overplay.util.MusicQueueHelper
import java.io.File


class BackgroundMusicService : Service(), MediaPlayer.OnPreparedListener,
    AudioManager.OnAudioFocusChangeListener {

    override fun onBind(p0: Intent?): IBinder = binder

    lateinit var mediaPlay: MediaPlayer
    var musicQueue: MutableList<MusicItem> = mutableListOf()
    var playPosition = 0

    private lateinit var viewmodel: OverPlayViewModel
    lateinit var currentlyPlayingMusic: CurrentlyPlayingMusic
    var loop: Boolean = false
    var playType = Constants.ALL_SONGS
    var playName: String? = null
    lateinit var activity: AppCompatActivity
    var imagePlaceHolder: Int? = null
    var pauseButton: Int? = null
    var playButton: Int? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private var binder = MyBinder()
    override fun onCreate() {
        //super.onCreate()
        mediaPlay = MediaPlayer()
        createChannel()

        //registerReceiver(broadcastReceiver, )
        registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
        audioManger = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(this@BackgroundMusicService)
                // setOnAudioFocusChangeListener(this@BackgroundMusicService, handler)

                build()
            }
        }


    }

    inner class MyBinder : Binder() {
        fun getService(): BackgroundMusicService = this@BackgroundMusicService
    }

    fun playMusic(
        queue: MutableList<MusicItem>,
        position: Int,
        model: OverPlayViewModel,
        repeat: Boolean = false, type: Int, passedActivity: AppCompatActivity,
        name: String, startMusic: Boolean = true
    ) {
        loop = repeat
        musicQueue = queue
        viewmodel = model
        playType = type
        activity = passedActivity
        val musicItem = musicQueue[position]
        currentlyPlayingMusic = SongsScreen.convertToPlay(musicItem, position)
        playName = name
        MusicQueueHelper.savePlayQueue(queue[position], passedActivity, type, name, position)

        viewmodel.insertPlay(currentlyPlayingMusic)

        musicItem.playCount = musicItem.playCount++
        musicItem.playTime = System.currentTimeMillis()
        viewmodel.updateSong(musicItem)
        playMusicAt(position, startMusic)



        mediaPlay.setOnCompletionListener {

            var nextPosition = if (loop)
                position
            else
                playPosition + 1

            if (nextPosition <= musicQueue.size - 1) {
                playMusic(
                    queue,
                    nextPosition,
                    model,
                    type = type,
                    passedActivity = activity,
                    name = playName!!
                ) //recursion

                playPosition = nextPosition
            }
        }

    }


    fun showNotification(resource_image: Int, playbackStateCompat: PlaybackStateCompat) {
        Glide.with(this).load(currentlyPlayingMusic.albumArt)
            .placeholder(imagePlaceHolder!!)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {

                    notify(resource_image, playbackStateCompat, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {

                    notify(resource_image, playbackStateCompat, errorDrawable!!)

                }
            })

    }

    fun notify(resource_image: Int, playbackStateCompat: PlaybackStateCompat, resource: Drawable) {
        var notification = CreateNotification.createNotification(
            this@BackgroundMusicService,
            currentlyPlayingMusic,
            resource_image,
            (resource as BitmapDrawable).bitmap,
            mediaPlay,
            callback, playbackStateCompat
        )
        startForeground(1, notification)
    }

    private fun playMusicAt(position: Int, startMusic: Boolean) {

        val clickedMusic = musicQueue[position]
        val source = clickedMusic.path

        val notificationIcon = if (startMusic)
            pauseButton!!
        else
            playButton!!

        playPosition = position

        if (mediaPlay != null) {
            mediaPlay.stop()
            mediaPlay.release()
        }

        if(File(source!!).exists()
        ){

        mediaPlay = MediaPlayer()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(source)
                prepare()
                mediaPlay=this
                if (startMusic) {

                    showNotification(notificationIcon, playBackPlay())
                    start()
                    requestAudioFocus()
                }else{

                    showNotification(notificationIcon, playBackPause())
                }

            }

        //TODO (find notification fix for the music duration)
        mediaPlay.setOnPreparedListener(this)

    }


    }

    fun seekTo(position: Int) {
        mediaPlay.seekTo(position)
    }

    fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManger.requestAudioFocus(focusRequest)
        } else {
            audioManger.requestAudioFocus(
                this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    fun dumpAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManger.abandonAudioFocusRequest(focusRequest)
        }
    }

    fun toggleMusic(checked: Boolean, checkState: Boolean = true) {
        if (checkState) {
            if (checked) {
                if (!mediaPlay.isPlaying) {

                    requestAudioFocus()
                    mediaPlay.start()
                    showNotification(pauseButton!!, playBackPlay())
                }
            } else {
                if (mediaPlay.isPlaying) {
                    mediaPlay.pause()
                    dumpAudioFocus()
                    showNotification(playButton!!, playBackPause())

                }
            }
        } else {
            if (mediaPlay.isPlaying) {
                dumpAudioFocus()
                mediaPlay.pause()

                showNotification(playButton!!, playBackPause())

            } else {

                showNotification(pauseButton!!, playBackPlay())
                requestAudioFocus()
                mediaPlay.start()
            }
        }
    }

    fun playNext() {

        var newPosition = playPosition + 1
        if (newPosition <= musicQueue.size - 1) {
            playMusic(
                musicQueue,
                newPosition,
                viewmodel,
                type = playType,
                passedActivity = activity,
                name = playName!!
            ) //recursion
            playPosition++
        } else {
            newPosition = 0
            playMusic(
                musicQueue,
                newPosition,
                viewmodel,
                type = playType,
                passedActivity = activity,
                name = playName!!
            ) //recursion
            playPosition = 0
        }
    }

    fun playPrevious() {
        var newPosition = playPosition - 1
        if (newPosition >= 0) {
            playMusic(
                musicQueue,
                newPosition,
                viewmodel,
                type = playType,
                passedActivity = activity,
                name = playName!!
            ) //recursion
            playPosition--

        } else {
            newPosition = musicQueue.size - 1
            playMusic(
                musicQueue,
                newPosition,
                viewmodel,
                type = playType,
                passedActivity = activity,
                name = playName!!
            ) //recursion
            playPosition = newPosition
        }
    }

    fun likeAction() {
        var playing = musicQueue[playPosition]
        playing.liked = !playing.liked
        viewmodel.updateSong(playing)
        currentlyPlayingMusic = SongsScreen.convertToPlay(playing, playPosition)
        if (mediaPlay.isPlaying) {
            showNotification(playButton!!, playBackPlay())
        } else {
            showNotification(playButton!!, playBackPlay())
        }

    }

    fun openFromNotification() {
        val intento = Intent(this, PlayMusicScreen::class.java)
            .apply {
                flags=Intent.FLAG_ACTIVITY_NEW_TASK
            }
        startActivity(intento)
    }

    fun bindViewModel(model: OverPlayViewModel) {

        viewmodel = model

    }

    var isPlaying = fun(): Boolean = mediaPlay.isPlaying
    override fun onPrepared(p0: MediaPlayer?) {

    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.extras!!.getString("actionname")
            when (action) {
                CreateNotification.ACTION_PREVIUOS -> playPrevious()

                CreateNotification.ACTION_PLAY ->
                    toggleMusic(checked = false, checkState = false)
                CreateNotification.ACTION_NEXT -> playNext()
                CreateNotification.ACTION_LIKE -> likeAction()
                CreateNotification.ACTION_OPEN -> openFromNotification()

            }
        }
    }

    var notificationManager: NotificationManager? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CreateNotification.CHANNEL_ID,
                "Overplay", NotificationManager.IMPORTANCE_MIN
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
    }

    val callback = object : MediaSessionCompat.Callback() {
        override fun onSeekTo(pos: Long) {
            // super.onSeekTo(pos)
            mediaPlay.seekTo(pos.toInt())
            if (mediaPlay.isPlaying) {

                showNotification(pauseButton!!, playBackPlay())
            } else {

                showNotification(playButton!!, playBackPause())
            }
        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {

            return true
        }
    }

    fun playBackPlay(): PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(
            PlaybackStateCompat.STATE_PLAYING,
            mediaPlay.currentPosition.toLong(),
            1f
        )
        .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()

    fun playBackPause(): PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(
            PlaybackStateCompat.STATE_PAUSED,
            mediaPlay.currentPosition.toLong(),
            1f
        )
        .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()

    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlay.stop()
        mediaPlay.release()
        return true
    }

    lateinit var audioManger: AudioManager

    val focusLock = Any()

    var playbackDelayed = false
    lateinit var focusRequest: AudioFocusRequest
    var playbackNowAuthorized = false

    override fun onAudioFocusChange(focus: Int) {
        if (focus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            toggleMusic(false)

        } else if (focus == AudioManager.AUDIOFOCUS_GAIN) {
            toggleMusic(true)
        } else if (focus == AudioManager.AUDIOFOCUS_LOSS) {
            toggleMusic(false)
        }
    }


}