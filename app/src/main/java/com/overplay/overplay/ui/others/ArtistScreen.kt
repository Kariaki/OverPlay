package com.overplay.overplay.ui.others

import com.votenoid.votenoid.Adapter.GeneralAdapter.items
import com.votenoid.votenoid.Adapter.GeneralAdapter.superClickListener
import com.votenoid.votenoid.Adapter.GeneralAdapter.viewHolderPlug
import com.overplay.overplay.adapter.ViewHolders.Plugs.SongsPlug.plug
import com.overplay.overplay.database.OverPlayViewModel.artistSongs
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.OverPlayViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.overplay.overplay.adapter.ViewHolders.Plugs.SongsPlug
import androidx.recyclerview.widget.LinearLayoutManager
import com.overplay.overplay.databinding.ArtistScreenBinding
import com.votenoid.votenoid.Adapter.SuperClickListener
import com.overplay.overplay.ui.others.ArtistScreenDirections.ActionArtistScreenToMusicOption
import java.util.ArrayList

class ArtistScreen : Fragment() {
    private var binding: ArtistScreenBinding? = null
    private var generalAdapter: GeneralAdapter? = null
    private var musicItems: List<MusicItem> = ArrayList()
    private var viewModel: OverPlayViewModel? = null
    private var args: ArtistScreenArgs? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ArtistScreenBinding.inflate(inflater)
        args = ArtistScreenArgs.fromBundle(arguments)
        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
        generalAdapter = GeneralAdapter()
        generalAdapter!!.items = musicItems
        generalAdapter!!.superClickListener = clickListener
        generalAdapter!!.viewHolderPlug = plug
        populateUser(args!!.artist)
        binding!!.musicList.hasFixedSize()
        binding!!.musicList.layoutManager = LinearLayoutManager(requireContext())
        binding!!.musicList.adapter = generalAdapter
        return binding!!.root
    }

    private fun populateUser(artist: String) {
        viewModel!!.artistSongs(artist).observe(viewLifecycleOwner, { items ->
            musicItems = items
            generalAdapter!!.notifyDataSetChanged()
        })
    }

    var clickListener: SuperClickListener = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val item = musicItems[position]
        }

        override fun onClickIcon(position: Int) {
            val action = ArtistScreenDirections.actionArtistScreenToMusicOption(
                musicItems[position]
            )
        }
    }
}