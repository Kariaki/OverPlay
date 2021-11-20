package com.overplay.overplay.adapter.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.overplay.overplay.adapter.FolderClickListener
import com.overplay.overplay.R
import com.overplay.overplay.database.entities.MusicItem
import com.votenoid.votenoid.Adapter.SuperClickListener

class FolderViewHolder(view:View):MainViewHolder(view) {

    lateinit var folderName:TextView
    lateinit var musicCount:TextView
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
        folderName=itemView.findViewById(R.id.folderName)
        musicCount=itemView.findViewById(R.id.musicCount)
        folderName.text=types.folder

        clickListener as FolderClickListener

        clickListener.callForEachItem(adapterPosition,musicCount)

        itemView.setOnClickListener {
            clickListener.onClickItem(adapterPosition)
        }
    }

}