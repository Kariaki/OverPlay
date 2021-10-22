package com.overplay.overplay.util

import com.overplay.overplay.database.entities.MusicItem
import com.overplay.overplay.database.entities.OptionItem

object Options {

    val option: MutableList<MusicItem> = mutableListOf(
        OptionItem("Like", 0),
        OptionItem("Play", 0),
        OptionItem("Delete", 0),
        OptionItem("Add to queue", 0),
        OptionItem("Add to playlist", 0)
    )

}