package com.overplay.overplay.ui.others

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.overplay.overplay.adapter.ViewHolders.MainViewHolder
import com.overplay.overplay.adapter.ViewHolders.MusicViewHolder
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.util.Constants
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.R
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.database.PlayViewModel
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener
import jp.wasabeef.glide.transformations.BlurTransformation

class AlbumSongsScreen : Fragment() {

    lateinit var musicList: RecyclerView
    var adapter = GeneralAdapter()
    var items: MutableList<MusicItem> = mutableListOf()

    private lateinit var viewModel: OverPlayViewModel
    var musicPlayer: MediaPlayer? = null
    private val args: AlbumSongsScreenArgs by navArgs()

    lateinit var albumDetails: MusicItem
    private var musicCount: Int? = null
    lateinit var backgroundBlur: ImageView
    lateinit var toolbar: CollapsingToolbarLayout
    lateinit var albumArtImage: ImageView
    lateinit var appBarLayout: AppBarLayout
    lateinit var albumNameView: TextView
    lateinit var artistNameView: TextView

    lateinit var playViewModel: PlayViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var contentView = inflater.inflate(R.layout.album_songs, container, false)
        musicList = contentView.findViewById(R.id.musicList)
        backgroundBlur = contentView.findViewById(R.id.backgroundBlur)
        albumArtImage = contentView.findViewById(R.id.albumArt)
        albumNameView = contentView.findViewById(R.id.albumName)
        artistNameView = contentView.findViewById(R.id.artistName)

        albumDetails = args.album
        viewModel =
            ViewModelProvider(this).get(OverPlayViewModel(activity?.application!!)::class.java)

        playViewModel = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)


        collapse(contentView)

        viewModel.albumSongs(args.albumName!!).observe(viewLifecycleOwner, {

            items = it
            adapter.items = it
            adapter.notifyDataSetChanged()
            //items = it
        })




        projectUi()



        adapter.superClickListener = clickListener
        adapter.viewHolderPlug = plug
        musicList.hasFixedSize()
        musicList.layoutManager = LinearLayoutManager(context)

        musicList.adapter = adapter

        return contentView
    }


    fun projectUi() {

        albumNameView.text = albumDetails.album
        artistNameView.text = albumDetails.artist

        Glide.with(requireContext()).load(albumDetails.albumArt)
            .placeholder(R.drawable.music_place_holder)
            .into(albumArtImage)

        Glide.with(requireContext()).load(albumDetails.albumArt)
            .placeholder(R.drawable.music_place_holder)
            .transform(BlurTransformation(25, 2))
            .into(backgroundBlur)


    }

    private val model: PlayViewModel by viewModels()

    val clickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val clickedMusic = items[position]

            myservice.playMusic(items, position, viewModel,type = Constants.ALBUM_SONGS,passedActivity = requireActivity() as AppCompatActivity,name = clickedMusic.album!!)
            playViewModel.setPlayValue(true)
            viewModel.insertPlay(SongsScreen.convertToPlay(clickedMusic, position))

        }

        override fun onClickIcon(position: Int) {
            super.onClickIcon(position)
        }
    }


    var plug = object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {

            var itemView =
                LayoutInflater.from(group.context).inflate(R.layout.album_music_item, group, false)
            return MusicViewHolder(itemView)
        }
    }


    private fun collapse(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        appBarLayout = view.findViewById(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    val animation = AnimationUtils.loadAnimation(context, R.anim.scale_down)
                    albumArtImage.startAnimation(animation)
                } else if (isShow) {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
                    albumArtImage.startAnimation(animation)
                    albumArtImage.setVisibility(View.VISIBLE)
                    albumArtImage.setVisibility(View.VISIBLE)
                    isShow = false
                }
            }
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
        }
    }
}