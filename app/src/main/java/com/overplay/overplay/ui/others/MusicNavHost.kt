package com.overplay.overplay.ui.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overplay.overplay.R


class MusicNavHost : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contentView = inflater.inflate(R.layout.music_nav_host, container, false)

        return contentView
    }

}