package com.overplay.overplay.Adapter.ViewHolders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.overplay.overplay.util.FunctionHelper
import com.votenoid.votenoid.Adapter.SuperClickListener

class MusicViewHolder(view: View) : MainViewHolder(view) {
    lateinit var tittle: TextView
    lateinit var artist: TextView
    lateinit var musicArt: ImageView

    lateinit var optionButton:ImageView
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
        tittle = itemView.findViewById(R.id.title)
        artist = itemView.findViewById(R.id.artist)
        musicArt = itemView.findViewById(R.id.musicArt)
        optionButton=itemView.findViewById(R.id.optionButton)
        //types as MusicItem
        tittle.text = types.tittle


        val artistText= FunctionHelper.processArtist(types.artist!!)
        artist.text = artistText
        Glide.with(context.applicationContext).load(types.albumArt).placeholder(R.drawable.music_place_holder).into(musicArt)


        itemView.setOnClickListener {
            clickListener.onClickItem(layoutPosition)
        }

        optionButton.setOnClickListener {
            clickListener.onClickIcon(layoutPosition)
        }

    }

}