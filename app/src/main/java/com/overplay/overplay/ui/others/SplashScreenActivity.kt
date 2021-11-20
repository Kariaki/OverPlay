package com.overplay.overplay.ui.others

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.overplay.overplay.MainActivity
import com.overplay.overplay.R
import com.overplay.overplay.ui.LoadMusicActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (!isFirstTime()) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        else {
            startActivity(Intent(this, LoadMusicActivity::class.java))
        }


    }

    private fun isFirstTime(): Boolean {
        val preference = getSharedPreferences("first_time", Context.MODE_PRIVATE)
        return preference.getBoolean("first_time", true)
    }

}