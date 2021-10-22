package com.overplay.overplay.screens.bottomNavigationScreens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.overplay.overplay.Adapter.ViewHolders.Plugs.SongsPlug
import com.overplay.overplay.notification.BackgroundMusicService
import com.overplay.overplay.util.Constants
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.PlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.SearchScreenBinding
import com.overplay.overplay.screens.SongsScreen
import com.overplay.overplay.util.ConnectionExecutor
import com.overplay.overplay.util.Connections
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener

class SearchScreen : Fragment() {


    lateinit var binding: SearchScreenBinding
    lateinit var viewModel: OverPlayViewModel
    lateinit var generalAdapter: GeneralAdapter
    var resultList: MutableList<MusicItem> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // var contentView = inflater.inflate(R.layout.search_screen, container, false)


        model = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)

        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
        generalAdapter = GeneralAdapter()
        binding = SearchScreenBinding.inflate(inflater, container, false)
        generalAdapter.apply {
            viewHolderPlug = SongsPlug.plug
            superClickListener = clickListen
        }
        binding.searchResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
            adapter = generalAdapter
        }

        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString()
                if (query.isNotEmpty()) {

                    search(query)
                } else {
                    resultList.clear()
                    generalAdapter.items = resultList
                    generalAdapter.notifyDataSetChanged()

                    binding.searchResult.visibility = View.GONE
                    binding.noResultLayout.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString()
                if (query.isNotEmpty()) {

                    search(query)

                } else {
                    resultList.clear()
                    generalAdapter.items = resultList
                    generalAdapter.notifyDataSetChanged()

                    binding.searchResult.visibility = View.GONE
                    binding.noResultLayout.visibility = View.VISIBLE
                }
            }
        })
        return binding.root

    }

    fun search(query: String) {
        viewModel.searchSongs("%$query%")
            .observe(viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    generalAdapter.items = it
                    resultList = it
                    generalAdapter.notifyDataSetChanged()

                    binding.searchResult.visibility = View.VISIBLE
                    binding.noResultLayout.visibility = View.GONE
                } else {

                    binding.searchResult.visibility = View.GONE
                    binding.noResultLayout.visibility = View.VISIBLE
                }
            })
    }

    lateinit var model: PlayViewModel


    override fun onResume() {
        super.onResume()
        if (binding.searchBox.text.toString().isNotEmpty()) {

            search(binding.searchBox.text.toString())

        } else {
            resultList.clear()
            generalAdapter.items = resultList
            generalAdapter.notifyDataSetChanged()

            binding.searchResult.visibility = View.GONE
            binding.noResultLayout.visibility = View.VISIBLE
        }
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

    private var clickListen = object : SuperClickListener {
        override fun onClickItem(position: Int) {
            val clickedMusic = resultList[position]

            myservice.playMusic(
                resultList,
                position,
                viewModel,
                type = Constants.SEARCH_SONGS, //TODO do search songs pls
                passedActivity = requireActivity() as AppCompatActivity,
                name = binding.searchBox.text.toString()
            )
            model.setPlayValue(true)

            viewModel.insertPlay(SongsScreen.convertToPlay(clickedMusic, position))

        }

        override fun onClickIcon(position: Int) {
            super.onClickIcon(position)
        }
    }

    lateinit var myservice: BackgroundMusicService

}