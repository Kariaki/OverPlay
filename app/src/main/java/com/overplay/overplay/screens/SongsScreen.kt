package com.overplay.overplay.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.Adapter.ViewHolders.Plugs.SongsPlug
import com.overplay.overplay.MusicOptionModal
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.util.Constants
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.database.PlayViewModel
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener

class SongsScreen : Fragment() {


    lateinit var musicList: RecyclerView
    var adapter = GeneralAdapter()
    var items: MutableList<MusicItem> = mutableListOf()

    private lateinit var viewModel: OverPlayViewModel
    private lateinit var playViewModel: PlayViewModel
    var musicPlayer: MediaPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contentView = inflater.inflate(R.layout.songs_screen, container, false)
        musicList = contentView.findViewById(R.id.musicList)
        viewModel =
            ViewModelProvider(this).get(OverPlayViewModel(activity?.application!!)::class.java)



        viewModel.allMusic.observe(viewLifecycleOwner, Observer {

            items = it
            adapter.items = it
            adapter.notifyDataSetChanged()
            //items = it
        })


        model = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)


        adapter.superClickListener = clickListener
        adapter.viewHolderPlug = SongsPlug.plug
        musicList.hasFixedSize()
        musicList.layoutManager = LinearLayoutManager(context)
        musicList.adapter = adapter


        return contentView
    }


    lateinit var model: PlayViewModel

    val clickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val clickedMusic = items[position]

            myservice.playMusic(
                items,
                position,
                viewModel,
                type = Constants.ALL_SONGS,
                passedActivity = requireActivity() as AppCompatActivity,
                name = ""
            )
            model.setPlayValue(true)

            viewModel.insertPlay(convertToPlay(clickedMusic, position))

        }

        override fun onClickIcon(position: Int) {
            //super.onClickIcon(position)
            val option=MusicOptionModal()
            option.show(childFragmentManager,null)
        }

    }


    override fun onDetach() {
        musicPlayer?.stop()
        super.onDetach()
    }

    companion object {
        fun convertToPlay(musicItem: MusicItem, position: Int): CurrentlyPlayingMusic =
            CurrentlyPlayingMusic(
                id = 1,
                artist = musicItem.artist,
                albumArt = musicItem.albumArt,
                album = musicItem.album,
                genre = musicItem.genre,
                displayName = musicItem.displayName,
                tittle = musicItem.tittle,
                path = musicItem.path,
                duration = musicItem.duration,
                musicId = musicItem.id,
                queueType = Constants.ALL_SONGS,
                folder = musicItem.folder,
                playCount = musicItem.playCount,
                playTime = musicItem.playTime,
                position = position,
                liked = musicItem.liked
            )

        fun convertBackToMusicItem(musicItem: CurrentlyPlayingMusic): MusicItem =
            MusicItem(
                id = musicItem.musicId,
                artist = musicItem.artist,
                albumArt = musicItem.albumArt,
                album = musicItem.album,
                genre = musicItem.genre,
                displayName = musicItem.displayName,
                tittle = musicItem.tittle,
                folder = musicItem.folder,

                playCount = musicItem.playCount,
                playTime = musicItem.playTime,
                path = musicItem.path,
                duration = musicItem.duration,

                )

    }

    override fun onStart() {
        super.onStart()

        activity?.bindService(
            Intent(activity, BackgroundMusicService::class.java),
            Connections(connectionExecutor = object : ConnectionExecutor {
                override fun execute(binderService: BackgroundMusicService) {
                    myservice = binderService
                    myservice.bindViewModel(viewModel)

                }

            }),
            Context.BIND_AUTO_CREATE
        )
    }

    lateinit var myservice: BackgroundMusicService

}