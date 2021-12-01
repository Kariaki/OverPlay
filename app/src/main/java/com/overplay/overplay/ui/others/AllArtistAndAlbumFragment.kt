package com.overplay.overplay.ui.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.R
import com.overplay.overplay.adapter.ViewHolders.AlbumViewHolder
import com.overplay.overplay.adapter.ViewHolders.AllArtistViewHolder
import com.overplay.overplay.adapter.ViewHolders.MainViewHolder
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.FragmentAlbumBinding
import com.overplay.overplay.util.Constants
import com.overplay.overplay.util.FunctionHelper
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding

    lateinit var musicList: RecyclerView
    lateinit var generalAdapter: GeneralAdapter
    var musicItem: MutableList<MusicItem> = mutableListOf()
    lateinit var viewModel: OverPlayViewModel
    val args: AlbumFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlbumBinding.inflate(inflater)
        generalAdapter = GeneralAdapter()
        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
        populateData()
        generalAdapter.apply {
            items = musicItem
            superClickListener = clickListener
            viewHolderPlug = plug
        }
        return binding.root
    }

    val plug = object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {
            val contentView = when(args.entryType){
                Constants.ALBUM_SONGS->{
                    AllArtistViewHolder( LayoutInflater.from(requireContext())
                        .inflate(R.layout.all_artist_design, group, false))
                }
                Constants.ARTIST_SONGS->{

                     AlbumViewHolder( LayoutInflater.from(requireContext())
                         .inflate(R.layout.all_artist_design, group, false))
                }

                }
            return contentView
        }
    }

    val clickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val item=musicItem[position]
            when (args.entryType) {
                Constants.ARTIST_SONGS -> {
                    val action=AlbumFragmentDirections.actionAlbumFragmentToLibraryMusicScreen2(
                        item.artist,
                        FunctionHelper.processArtist(item.artist!!),
                        Constants.ARTIST_SONGS)
                    findNavController().navigate(action)
                }
                Constants.ALBUM_SONGS -> {
                    val action=AlbumFragmentDirections.actionAlbumFragmentToAlbumSongs(item.album,item)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun populateData() {
        when (args.entryType) {
            Constants.ARTIST_SONGS -> {
                viewModel.allArtist.observe(viewLifecycleOwner) {
                    musicItem = it
                    generalAdapter.items = it
                    generalAdapter.notifyDataSetChanged()
                }

            }
            Constants.ALBUM_SONGS -> {
                viewModel.allAlbums.observe(viewLifecycleOwner) {
                    musicItem = it
                    generalAdapter.items = it
                    generalAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}