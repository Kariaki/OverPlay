package com.overplay.overplay.Adapter.ViewHolders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.votenoid.votenoid.Adapter.SuperClickListener

class RecentlyPlayedMusicViewHolder(view: View) : MainViewHolder(view) {
    lateinit var albumArt: ImageView
    lateinit var playText: TextView
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
        albumArt = itemView.findViewById(R.id.albumArt)
        playText = itemView.findViewById(R.id.playText)
        playText.text = types.tittle
        Glide.with(context).load(types.albumArt).placeholder(R.drawable.music_place_holder).into(albumArt)

        itemView.setOnClickListener {
            clickListener.onClickItem(adapterPosition)
        }
    }
}