package com.overplay.overplay.Adapter.ViewHolders.Plugs

import android.view.LayoutInflater
import android.view.ViewGroup
import com.overplay.overplay.Adapter.ViewHolders.MainViewHolder
import com.overplay.overplay.Adapter.ViewHolders.OptionViewHolder
import com.overplay.overplay.R
import com.votenoid.votenoid.Adapter.GeneralAdapter

object OptionPlug {

    val plug= object : GeneralAdapter.ViewHolderPlug {
        override fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder {

            val contentView=LayoutInflater.from(group.context).inflate(R.layout.music_option_list_dialog_item,group,false)
            return OptionViewHolder(contentView)
        }
    }
}