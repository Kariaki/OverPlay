package com.overplay.overplay.Adapter

import android.widget.TextView
import com.votenoid.votenoid.Adapter.SuperClickListener

abstract class FolderClickListener():SuperClickListener {

    abstract fun callForEachItem(position:Int,counter:TextView);
}