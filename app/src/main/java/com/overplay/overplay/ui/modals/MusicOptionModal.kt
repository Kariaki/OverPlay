package com.overplay.overplay

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.MusicOptionsBinding
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.ui.modals.DeleteMusicDialog
import com.overplay.overplay.ui.others.SongsScreen
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.overplay.overplay.util.Constants
import com.overplay.overplay.util.FunctionHelper
import java.io.File


class MusicOptionModal : BottomSheetDialogFragment() {


    lateinit var binding: MusicOptionsBinding
    private val args: MusicOptionModalArgs by navArgs()
    lateinit var viewModel: OverPlayViewModel

    lateinit var musicItem: MusicItem
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        musicItem = args.musicItem

        viewModel =
            ViewModelProvider(this).get(OverPlayViewModel(requireActivity().application)::class.java)

        viewModel.currentlyPlayingMusic.observe(viewLifecycleOwner) {
            //TODO CHECK IF MUSIC IN ARGS IS the CURRENT PLAYING MUSIC
        }

        binding = MusicOptionsBinding.inflate(inflater)

        binding.artistName.text = FunctionHelper.processArtist(musicItem.artist!!)
        binding.songName.text = musicItem.tittle
        binding.musicLikeState.isChecked = musicItem.liked
        Glide.with(requireContext()).load(musicItem.albumArt).placeholder(R.drawable.music_place_holder).into(binding.albumArt)

        optionClickEvents()

        return binding.root
    }

    private fun openArtistPage() {
        val action =
            MusicOptionModalDirections.actionMusicOptionToArtistScreen(args.musicItem.artist!!)
        findNavController().navigate(action)
    }

    private fun openAlbumScreen() {
        val action = MusicOptionModalDirections.actionMusicOptionToAlbumSongs(
            args.musicItem.album,
            args.musicItem
        )
        findNavController().navigate(action)
    }

    private fun optionClickEvents() {
        binding.viewAlbum.setOnClickListener {
            openAlbumScreen()

        }
        binding.viewArtist.setOnClickListener {
            openArtistPage()
        }
        binding.playNext.setOnClickListener {

            myservice.playNext(args.musicItem)

        }

        binding.addToPlayList.setOnClickListener {
            //TODO NOT IMPLEMENTED
        }
        binding.addToQueue.setOnClickListener {
            myservice.addToQueue(args.musicItem)
        }
        binding.deleteSong.setOnClickListener {

        }
        //like music click
        binding.musicLikeState.setOnCheckedChangeListener { p0, p1 -> likeClick(p1) }
    }

    private fun likeClick(checked: Boolean) {
        musicItem.liked = checked
        viewModel.updateSong(musicItem)
    }


    private fun deleteSong() {
        // TODO SHOW DELETE DIALOG AND DELETE FROM VIEWMODEL BEFORE DELETING FROM DEVICE
        val dialog = DeleteMusicDialog(object : DeleteMusicDialog.OptionClickListener {

            override fun deleteClick() {
                TODO("Not yet implemented")
            }
        })
        dialog.show(childFragmentManager, null)
    }

    private fun deleteFromDatabase(musicItem: MusicItem) {
        viewModel.deleteSong(musicItem)
    }

    private fun deleteFromDevice(musicItem: MusicItem) {
        val musicFile = File(musicItem.path!!)
        if (musicFile.exists())
            musicFile.delete()

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