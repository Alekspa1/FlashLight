package com.exampl3.flashlight.Presentation

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.exampl3.flashlight.R
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            setTheme(R.style.theme_35)
        }
    }
}
