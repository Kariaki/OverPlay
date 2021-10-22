package com.overplay.overplay.Adapter.ViewHolders

import android.content.Context
import android.view.View
import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.entities.OptionItem
import com.votenoid.votenoid.Adapter.SuperClickListener

class OptionViewHolder(view: View) : MainViewHolder(view) {
    override fun bindPostType(
        types: MusicItem,
        context: Context,
        clickListener: SuperClickListener
    ) {
        types as OptionItem

    }
}