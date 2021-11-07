package com.overplay.overplay.Adapter.ViewHolders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.R
import com.overplay.overplay.util.FunctionHelper
import com.votenoid.votenoid.Adapter.SuperClickListener

class AlbumMusicViewHolder(view:View):MainViewHolder(view) {
    lateinit var tittle: TextView
    lateinit var artist: TextView
    lateinit var optionButton:ImageView
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
        tittle = itemView.findViewById(R.id.title)

        optionButton=itemView.findViewById(R.id.optionButton)
        artist = itemView.findViewById(R.id.artist)
        //types as MusicItem
        tittle.text = types.tittle

        // var id=types.genre

        artist.text = FunctionHelper.processArtist(types.artist!!)

        itemView.setOnClickListener {
            clickListener.onClickItem(adapterPosition)
        }

        optionButton.setOnClickListener {
            clickListener.onClickIcon(adapterPosition)
        }
    }
}