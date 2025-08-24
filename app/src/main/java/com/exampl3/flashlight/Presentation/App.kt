package com.exampl3.flashlight.Presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.exampl3.flashlight.R
import dagger.hilt.android.HiltAndroidApp



@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}
