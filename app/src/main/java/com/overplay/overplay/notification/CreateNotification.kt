package com.overplay.overplay.notification

import android.annotation.SuppressLint
import android.app.Notification
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import android.os.Build
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.overplay.overplay.R
import com.overplay.overplay.util.FunctionHelper

object CreateNotification {
    const val CHANNEL_ID = "OVERPLAY_NOTIFICATION"
    const val ACTION_PREVIUOS = "Previous"
    const val ACTION_PLAY = "Play"
    const val ACTION_LIKE = "Like"
    const val ACTION_NEXT = "Next"
    const val ACTION_OPEN= "Open"
    var notification: Notification? = null
    @SuppressLint("LaunchActivityFromNotification")
    fun createNotification(
        context: Context?,
        track: CurrentlyPlayingMusic,
        playbutton: Int,
        icon: Bitmap?,
        mediaPlayer: MediaPlayer,
        callback: MediaSessionCompat.Callback?,
        state: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PAUSED,
                mediaPlayer.currentPosition.toLong(),
                1f
            )

            .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
    ): Notification? {

        val mediaSessionCompat = MediaSessionCompat(context!!, "BackgroundMusicService")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaSessionCompat.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, track.albumArt)

                    .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.duration.toLong())
                    .putBitmap(MediaMetadata.METADATA_KEY_ART, icon)
                    .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, icon)

                    .build()
            )
            mediaSessionCompat.setCallback(callback)
            mediaSessionCompat.setPlaybackState(state)

            val pendingIntentPrevious: PendingIntent
            val intentPrevious = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PREVIUOS)
            pendingIntentPrevious = PendingIntent.getBroadcast(
                context, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val pendingIntentNext: PendingIntent
            val intentNext = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_NEXT)
            pendingIntentNext = PendingIntent.getBroadcast(
                context, 0,
                intentNext, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val intentLike = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_LIKE)
            val pendingIntentLike = PendingIntent.getBroadcast(
                context, 0,
                intentLike, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val intentClick = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_OPEN)
            val pendingIntentClick = PendingIntent.getBroadcast(
                context, 0,
                intentClick, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val like: Int = if (track.liked) R.drawable.favorite_close else R.drawable.favorite_open

            val artistText= FunctionHelper.processArtist(track.artist!!)

            //create notification
            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.over_play)
                .setContentTitle(track.tittle)
                .setContentText(artistText)
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntentClick)
                .setShowWhen(false)
                .addAction(R.drawable.previous, ACTION_PREVIUOS, pendingIntentPrevious)
                .addAction(playbutton, ACTION_PLAY, pendingIntentPlay)
                .addAction(R.drawable.next, ACTION_NEXT, pendingIntentNext)
                .addAction(like, ACTION_LIKE, pendingIntentLike)
                .setStyle(
                    // mediaStyle
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2, 3)
                        .setMediaSession(mediaSessionCompat.sessionToken)


                )
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setTicker(track.tittle)
                .setProgress(mediaPlayer.duration,mediaPlayer.currentPosition,false)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

            // var noficationManagerCompat=NotificationManagerCompat
            //  notificationManagerCompat.notify(1, notification);
        }
        return notification
    }
}