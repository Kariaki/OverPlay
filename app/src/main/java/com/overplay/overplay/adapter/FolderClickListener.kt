package com.overplay.overplay.adapter

import android.widget.TextView
import com.votenoid.votenoid.Adapter.SuperClickListener

abstract class FolderClickListener():SuperClickListener {

    abstract fun callForEachItem(position:Int,counter:TextView);
}