package com.overplay.overplay

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.MusicOptionsBinding


class MusicOptionModal : BottomSheetDialogFragment() {


    lateinit var binding: MusicOptionsBinding
    private val args: MusicOptionModalArgs by navArgs()
    lateinit var viewModel:OverPlayViewModel

    lateinit var musicItem:MusicItem
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         musicItem = args.musicItem

        viewModel = ViewModelProvider(this).get(OverPlayViewModel(requireActivity().application)::class.java)

        viewModel.currentlyPlayingMusic.observe(viewLifecycleOwner){
            //TODO CHECK IF MUSIC IN ARGS IS the CURRENT PLAYING MUSIC
        }

        binding = MusicOptionsBinding.inflate(inflater)

        binding.artistName.text = (musicItem.artist!!)
        binding.songName.text = musicItem.tittle
        binding.musicLikeState.isChecked=musicItem.liked
        Glide.with(requireContext()).load(musicItem.albumArt).into(binding.albumArt)

        return binding.root
    }

    private fun likeClick(checked:Boolean){
        musicItem.liked=checked
        viewModel.updateSong(musicItem)
    }

}