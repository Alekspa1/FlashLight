package com.exampl3.flashlight.Data.sharedPreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.exampl3.flashlight.Const.ALARM_SETTINGS
import com.exampl3.flashlight.Const.SIZE_SETTINGS
import com.exampl3.flashlight.Const.SIZE_STANDART


import com.exampl3.flashlight.Const.SORT_SETTINGS
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Const.THEME_SETTINGS
import com.exampl3.flashlight.Const.URI_OLD
import com.exampl3.flashlight.Const.URI_STANDART
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

    fun getSize(): String? = prefSettings.getString(SIZE_SETTINGS, SIZE_STANDART)

    fun saveSize(value: String){
        editPremium.putString(SIZE_SETTINGS, value)
        editPremium.apply()
    }

    fun getUriAlarm(): String? = prefSettings.getString(ALARM_SETTINGS, URI_STANDART)

    fun saveUriAlarm(uri: Uri){
        editPremium.putString(ALARM_SETTINGS, uri.toString())
        editPremium.apply()
    }

    fun getOldUriAlarm(): String? = prefSettings.getString(URI_OLD, URI_STANDART)

    fun saveOldUriAlarm(uri: Uri){
        editPremium.putString(URI_OLD, uri.toString())
        editPremium.apply()
    }
}