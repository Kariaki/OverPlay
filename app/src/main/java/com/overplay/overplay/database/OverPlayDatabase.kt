package com.overplay.overplay.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.overplay.overplay.database.entities.CurrentlyPlayingMusic
import com.overplay.overplay.database.entities.MusicItem

@Database(entities = [MusicItem::class, CurrentlyPlayingMusic::class],exportSchema = false,version = 9)
abstract class OverPlayDatabase:RoomDatabase() {


    abstract fun musicDao():OverPlayDao

    companion object{
        @Volatile
        private var INSTANCE:OverPlayDatabase?=null

        fun getDatabase(application: Application):OverPlayDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    application,
                    OverPlayDatabase::class.java,
                    "database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE=instance
                return instance
            }
        }
    }
}