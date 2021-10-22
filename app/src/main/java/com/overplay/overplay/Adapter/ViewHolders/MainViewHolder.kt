package com.overplay.overplay.Adapter.ViewHolders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.database.entities.MusicItem
import com.votenoid.votenoid.Adapter.SuperClickListener

abstract class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

     abstract fun  bindPostType(types: MusicItem, context:Context, clickListener: SuperClickListener)


}