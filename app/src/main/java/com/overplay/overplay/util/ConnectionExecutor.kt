package com.overplay.overplay.util

import com.overplay.overplay.notification.BackgroundMusicService

interface ConnectionExecutor {

    fun execute(binderService: BackgroundMusicService);
}