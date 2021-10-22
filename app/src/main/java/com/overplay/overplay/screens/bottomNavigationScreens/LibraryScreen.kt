package com.overplay.overplay.screens.bottomNavigationScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.Adapter.FolderClickListener
import com.overplay.overplay.Adapter.ViewHolders.Plugs.SongsPlug
import com.overplay.overplay.util.Constants
import com.overplay.overplay.R
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.OverPlayViewModel
import com.votenoid.votenoid.Adapter.GeneralAdapter


class LibraryScreen : Fragment() {

    lateinit var likePage: LinearLayout
    lateinit var folderList: RecyclerView
    var folders: MutableList<MusicItem> = mutableListOf()
    lateinit var viewModel: OverPlayViewModel
    lateinit var likedSongsCount: TextView
    lateinit var generalAdapter: GeneralAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        generalAdapter = GeneralAdapter()
        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.library_screen, container, false)
        likePage = contentView.findViewById(R.id.likePage)
        folderList = contentView.findViewById(R.id.folderList)
        likedSongsCount = contentView.findViewById(R.id.likedSongsCount)


        generalAdapter.apply {

            viewHolderPlug = SongsPlug.folderPlug
            superClickListener = clicklisten

        }
        likePage.setOnClickListener {
            openMusicPage()
        }
        folderList.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = generalAdapter
        }
        populateFolders()
        viewModel.likedSongs.observe(viewLifecycleOwner, {

            if (it.isNotEmpty()) {
                val text = "${it.size} liked songs"
                likedSongsCount.text = text
            } else {
                likedSongsCount.text = "No liked songs"
            }
        })

        return contentView
    }

    fun openMusicPage() {
        val action =
            LibraryScreenDirections.actionLibraryScreenToFolderMusicScreen2(
                "",
                "Liked songs",
                Constants.LIKED_SONGS
            )
        findNavController().navigate(action)
    }

    fun populateFolders() {

        viewModel.folders.observe(viewLifecycleOwner, {
            folders = it
            generalAdapter.items = it
            generalAdapter.notifyDataSetChanged()

        })
    }

    val clicklisten = object : FolderClickListener() {
        override fun onClickItem(position: Int) {
            val clickFolder = folders[position]
            val action =
                LibraryScreenDirections.actionLibraryScreenToFolderMusicScreen2(
                    clickFolder.folder!!,
                    clickFolder.folder!!,
                    Constants.FOLDER_SONGS
                )
            findNavController().navigate(action)
        }

        override fun callForEachItem(position: Int, counter: TextView) {
            val musicItem = folders[position]

            if (musicItem.folder != null) {
                viewModel.getFolderCount(musicItem.folder!!)
                    .observe(viewLifecycleOwner, {
                        if (it != null) {
                            val count = it
                            val text = "$count songs"
                            counter.text = text
                        }
                    })

            }


        }
    }

}