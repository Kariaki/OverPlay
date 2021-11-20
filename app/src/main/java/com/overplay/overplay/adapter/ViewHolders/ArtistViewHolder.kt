package com.overplay.overplay.adapter.ViewHolders

import android.content.Context
import android.view.View
import com.overplay.overplay.database.entities.MusicItem
import com.votenoid.votenoid.Adapter.SuperClickListener

class ArtistViewHolder(view: View):MainViewHolder(view) {

    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
    }
}