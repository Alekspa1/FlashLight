package com.exampl3.flashlight.Domain.model.sharedPreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.exampl3.flashlight.Const
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceImpl @Inject constructor(
    context: Application) {
    private val  greetings = "Дорогие пользователи! \nВвиду особенности некоторых моделей телефонов," +
            " установленные напоминания сбиваются после перезагрузки устройства," +
            " если вы столкнулись с такой проблемой, вам необходимо в настройках приложения," +
            " включить автозапуск приложения или разрешить приложению работать в фоновом режиме. " +
            "Либо повторно входить в приложение после перезагрузки, чтобы напоминания обновились."


    private var  prefPremium: SharedPreferences = context.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
    private val editPremium: SharedPreferences.Editor = prefPremium.edit()
     fun getPremium(): Boolean {
        return prefPremium.getBoolean(Const.premium_KEY, false)
    }
     fun savePremium(flag: Boolean) {
         editPremium.putBoolean(Const.premium_KEY, flag)
         editPremium.apply()
    }
    private val  prefNotebook: SharedPreferences = context.getSharedPreferences("TABLE", Context.MODE_PRIVATE)
    private val editNotebook: SharedPreferences.Editor = prefNotebook.edit()
    fun getStringNoteBook(): String? {
        return prefNotebook.getString(Const.keyNoteBook, greetings)
    }
    fun saveStringNoteBook(value: String) {
        editNotebook.putString(Const.keyNoteBook, value)
        editNotebook.apply()
    }

}