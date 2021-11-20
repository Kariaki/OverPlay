package com.overplay.overplay.adapter.ViewHolders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.votenoid.votenoid.Adapter.SuperClickListener

class AlbumViewHolder(view: View) : MainViewHolder(view) {

    lateinit var albumArt:ImageView
    lateinit var artistName:TextView
    lateinit var albumTittle:TextView
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {

        albumArt=itemView.findViewById(R.id.albumArt)
        artistName=itemView.findViewById(R.id.artistName)
        albumTittle=itemView.findViewById(R.id.albumTittle)
        Glide.with(context).load(types.albumArt).placeholder(R.drawable.music_place_holder).into(albumArt)

        albumTittle.text=types.album
        artistName.text=types.artist



        itemView.setOnClickListener {
            clickListener.onClickItem(adapterPosition)
        }


    }
}