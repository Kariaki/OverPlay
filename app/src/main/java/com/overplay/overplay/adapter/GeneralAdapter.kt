package com.votenoid.votenoid.Adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.overplay.overplay.adapter.ViewHolders.MainViewHolder
import com.overplay.overplay.database.entities.MusicItem

class GeneralAdapter : RecyclerView.Adapter<MainViewHolder>() {

    var items= mutableListOf<MusicItem>()


    interface  ViewHolderPlug{
        fun setPlug(group: ViewGroup, viewType: Int): MainViewHolder
    }
    lateinit var viewHolderPlug:ViewHolderPlug

    lateinit var superClickListener:SuperClickListener



    fun setviewHolderPlug(plug:ViewHolderPlug){
        viewHolderPlug=plug
    }
    lateinit var CONTEXT:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        CONTEXT=parent.context

        return viewHolderPlug.setPlug(parent,viewType)

    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindPostType(items.get(position),CONTEXT,superClickListener)

    }

    override fun getItemViewType(position: Int): Int {
        return items.get(position).type
    }
    override fun getItemCount(): Int=items.size

}