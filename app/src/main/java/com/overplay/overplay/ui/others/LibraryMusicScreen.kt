package com.overplay.overplay.ui.others

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.adapter.ViewHolders.Plugs.SongsPlug
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.util.Constants
import com.overplay.overplay.R
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.database.PlayViewModel
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener

class LibraryMusicScreen : Fragment() {

    lateinit var musicList: RecyclerView
    lateinit var generalAdapter: GeneralAdapter
    var musicItem: MutableList<MusicItem> = mutableListOf()
    private val args: LibraryMusicScreenArgs by navArgs()
    lateinit var viewModel: OverPlayViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.library_music_screen, container, false)
        musicList = contentView.findViewById(R.id.musicList)
        generalAdapter = GeneralAdapter()
        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
        model = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)

        generalAdapter.apply {
            superClickListener = clickListener
            viewHolderPlug = SongsPlug.plug
        }
        generateMusicItems()
        musicList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
            adapter = generalAdapter
        }

        return contentView
    }

    private fun generateMusicItems() {
        when (args.type) {
            Constants.FOLDER_SONGS ->
                viewModel.folderSongs(args.queryText).observe(viewLifecycleOwner, {
                    generalAdapter.items = it
                    musicItem = it
                    generalAdapter.notifyDataSetChanged()
                })
            Constants.LIKED_SONGS ->

                viewModel.likedSongs.observe(viewLifecycleOwner, {
                    generalAdapter.items = it
                    musicItem = it
                    generalAdapter.notifyDataSetChanged()
                })
        }
    }

    val clickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val clickedMusic = musicItem[position]

            myservice.playMusic(
                musicItem,
                position,
                viewModel,
                type = args.type,
                passedActivity = requireActivity() as AppCompatActivity,
                name = args.queryText
            )
            model.setPlayValue(true)

            viewModel.insertPlay(SongsScreen.convertToPlay(clickedMusic, position))

        }

        override fun onClickIcon(position: Int) {
            super.onClickIcon(position)
        }
    }

    lateinit var myservice: BackgroundMusicService

    lateinit var model: PlayViewModel
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

}