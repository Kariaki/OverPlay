package com.overplay.overplay.ui.bottomNavigationScreens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.adapter.ViewHolders.AlbumViewHolder
import com.overplay.overplay.adapter.ViewHolders.ArtistViewHolder
import com.overplay.overplay.adapter.ViewHolders.MainViewHolder
import com.overplay.overplay.adapter.ViewHolders.RecentlyPlayedMusicViewHolder
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.util.Constants
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.overplay.overplay.ui.others.SongsScreen
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.database.PlayViewModel
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener

class OverPlayHome : Fragment() {

    lateinit var albumList: RecyclerView
    lateinit var artistList: RecyclerView
    lateinit var albumAdapter: GeneralAdapter
    lateinit var artistAdapter: GeneralAdapter
    lateinit var recentlyPlayedAdapter: GeneralAdapter
    var albumItems: MutableList<MusicItem> = mutableListOf()
    var artistListItems: MutableList<MusicItem> = mutableListOf()
    var recentlyPlayedItemList: MutableList<MusicItem> = mutableListOf()
    lateinit var allSongs: RelativeLayout
    private lateinit var viewModel: OverPlayViewModel
    lateinit var recentlyPlayedList: RecyclerView

    lateinit var recentlyPlayedHolder: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.over_play_home, container, false)

        albumList = contentView.findViewById(R.id.albumList)
        artistList = contentView.findViewById(R.id.artistList)
        allSongs = contentView.findViewById(R.id.allSongs)
        recentlyPlayedList = contentView.findViewById(R.id.recentlyPlayedList)
        recentlyPlayedHolder = contentView.findViewById(R.id.recentlyPlayedHolder)
        recentlyPlayedAdapter = GeneralAdapter()


        viewModel =
            ViewModelProvider(this).get(OverPlayViewModel(activity?.application!!)::class.java)

        model = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)

        fetchRecentlyPlayed()

        albumAdapter = GeneralAdapter()
        artistAdapter = GeneralAdapter()

        albumAdapter.apply {
            viewHolderPlug = albumPlug
            superClickListener = albumClickListener

        }
        //add albums to list
        fetchAlbums()

        artistAdapter.apply {
            viewHolderPlug = artistPlug
            superClickListener = clickListen
            items = artistListItems
        }

        albumList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        artistList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        recentlyPlayedAdapter.apply {

            viewHolderPlug = recentlyPlayedPlug
            superClickListener = recentPlayedClick

        }

        recentlyPlayedList.apply {

            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = recentlyPlayedAdapter
        }


        albumList.adapter = albumAdapter
        artistList.adapter = artistAdapter


        allSongs.setOnClickListener {

            val action = OverPlayHomeDirections.actionOverPlayHomeToSongsScreen()

            findNavController().navigate(action)
        }

        return contentView
    }

    // add recently played from viewmodel
    private fun fetchRecentlyPlayed() {
        viewModel.recentlyPlayed.observe(viewLifecycleOwner, {
            recentlyPlayedItemList = it
            //  if (recentlyPlayedItemList[0].playCount != 0) {

            recentlyPlayedHolder.visibility = View.VISIBLE
            recentlyPlayedAdapter.items = recentlyPlayedItemList
            recentlyPlayedAdapter.notifyDataSetChanged()

        })
    }

    var albumPlug = object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {
            val plug = LayoutInflater.from(context).inflate(R.layout.album_design, group, false)

            return AlbumViewHolder(plug)
        }
    }
    var artistPlug = object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {
            val plug = LayoutInflater.from(context).inflate(R.layout.artist_design, group, false)

            return ArtistViewHolder(plug)
        }
    }
    var recentlyPlayedPlug = object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {
            val plug =
                LayoutInflater.from(context).inflate(R.layout.recently_played_design, group, false)

            return RecentlyPlayedMusicViewHolder(plug)
        }
    }

    var clickListen = object : SuperClickListener {
        override fun onClickItem(position: Int) {

        }
    }


    lateinit var model: PlayViewModel

    var recentPlayedClick = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val clickedMusic = recentlyPlayedItemList[position]
            //update music details upon click
            myservice.playMusic(
                recentlyPlayedItemList,
                position,
                viewModel,
                type = Constants.RECENTLY_PLAYED_SONGS,
                passedActivity = requireActivity() as AppCompatActivity,
                name = ""
            )
            model.setPlayValue(true)

            viewModel.insertPlay(SongsScreen.convertToPlay(clickedMusic, position))
        }
    }
    var albumClickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {

            val action =
                OverPlayHomeDirections.actionOverPlayHomeToAlbumSongs(
                    albumItems[position].album!!,
                    albumItems[position]
                )
            findNavController().navigate(action)
        }
    }

    fun fetchAlbums() {

        viewModel.allAlbums.observe(viewLifecycleOwner,  {
            albumItems = it
            albumAdapter.items = it
            albumAdapter.notifyDataSetChanged()

        })

    }

    override fun onStart() {
        super.onStart()
        activity?.bindService(
            Intent(activity, BackgroundMusicService::class.java),
            Connections(executor),
            Context.BIND_AUTO_CREATE
        )
    }

    lateinit var myservice: BackgroundMusicService

    var executor = object : ConnectionExecutor {
        override fun execute(binderService: BackgroundMusicService) {
            myservice = binderService
            myservice.playButton=R.drawable.play_alt
            myservice.pauseButton=R.drawable.pause_alt
            myservice.imagePlaceHolder=R.drawable.music_place_holder
        }
    }
}