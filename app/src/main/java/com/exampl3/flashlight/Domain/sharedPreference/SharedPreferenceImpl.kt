package com.exampl3.flashlight.Domain.sharedPreference

import android.content.SharedPreferences
import com.exampl3.flashlight.Data.Const
import javax.inject.Inject

class SharedPreferenceImpl @Inject constructor(private val pref: SharedPreferences) {

    private val edit: SharedPreferences.Editor = pref.edit()
     fun getSP(): Boolean {
        return pref.getBoolean(Const.premium_KEY, false)
    }
     fun saveSP(flag: Boolean) {
        edit.putBoolean(Const.premium_KEY, flag)
        edit.apply()
    }

}