package com.nikanorov.newsjetminiflux.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import com.nikanorov.newsjetminiflux.utils.rememberWindowSizeClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val windowSizeClass = rememberWindowSizeClass()
            NewsjetApp(windowSizeClass)
        }
    }
}
