package com.exampl3.flashlight.Domain.sharedPreference

import com.exampl3.flashlight.Data.Const

interface SharedPreferenceRepository {
    fun getSP() : Boolean
    fun saveSP(flag: Boolean)


}