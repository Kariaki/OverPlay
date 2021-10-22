package com.overplay.overplay.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.overplay.overplay.Adapter.ViewHolders.Plugs.SongsPlug
import com.overplay.overplay.R
import com.overplay.overplay.database.OverPlayViewModel
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.databinding.SearchQueryScreenBinding
import com.overplay.overplay.databinding.SearchScreenBinding
import com.votenoid.votenoid.Adapter.GeneralAdapter
import com.votenoid.votenoid.Adapter.SuperClickListener


class SearchQueryScreen : Fragment() {

    lateinit var binding: SearchQueryScreenBinding
    lateinit var viewModel: OverPlayViewModel
    lateinit var generalAdapter: GeneralAdapter
    var resultList: MutableList<MusicItem> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity()).get(OverPlayViewModel::class.java)
        generalAdapter = GeneralAdapter()
        binding = SearchQueryScreenBinding.inflate(inflater, container, false)
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
                val query=p0.toString()
                if(query.isNotEmpty()){

                    search(query)
                }else{
                    resultList.clear()
                    generalAdapter.items=resultList
                    generalAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                val query=p0.toString()
                if(query.isNotEmpty()){

                    search(query)
                }else{
                    resultList.clear()
                    generalAdapter.items=resultList
                    generalAdapter.notifyDataSetChanged()
                }
            }
        })
        return binding.root
    }

    fun search(query: String) {
        viewModel.searchSongs("%$query%")
            .observe(viewLifecycleOwner, {
                generalAdapter.items = it
                resultList = it
                generalAdapter.notifyDataSetChanged()
            })


    }

    var clickListen = object : SuperClickListener {
        override fun onClickItem(position: Int) {

        }
    }
}