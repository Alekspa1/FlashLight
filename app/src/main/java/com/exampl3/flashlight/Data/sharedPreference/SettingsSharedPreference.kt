package com.exampl3.flashlight.Data.sharedPreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


import com.exampl3.flashlight.Const.SORT_SETTINGS
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Const.THEME_SETTINGS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsSharedPreference @Inject constructor(
    context: Application
){
    private var prefSettings: SharedPreferences =
        context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
    private val editPremium: SharedPreferences.Editor = prefSettings.edit()

    fun getSort(): String? = prefSettings.getString(SORT_SETTINGS, SORT_STANDART)

    fun saveSort(value: String){
        editPremium.putString(SORT_SETTINGS, value)
        editPremium.apply()
    }

    fun getTheme(): String? = prefSettings.getString(THEME_SETTINGS, THEME_FUTURE)

    fun saveTheme(value: String){
        editPremium.putString(THEME_SETTINGS, value)
        editPremium.apply()
    }
}