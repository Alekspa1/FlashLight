package com.exampl3.flashlight.Data.sharedPreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.exampl3.flashlight.Const
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceImpl @Inject constructor(
    context: Application
) {
    private val greetings =
        "Дорогие пользователи! \nВвиду особенности некоторых моделей телефонов," +
                " установленные напоминания сбиваются после перезагрузки устройства," +
                " если вы столкнулись с такой проблемой, вам необходимо в настройках приложения," +
                " включить автозапуск приложения или разрешить приложению работать в фоновом режиме. " +
                "Либо повторно входить в приложение после перезагрузки, чтобы напоминания обновились." +
                "Если вам понравится мое приложение, то буду рад если оставите хороший отзыв," +
                " также если у вас будут вопросы или предложения по улучшению приложения в настройках есть кнопка обратной связи и инструкция " +
                "к приложению."


    private var prefPremium: SharedPreferences =
        context.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
    private val editPremium: SharedPreferences.Editor = prefPremium.edit()

    fun getPremium(): Boolean = prefPremium.getBoolean(Const.PREMIUM_KEY, true)

    fun savePremium(flag: Boolean) {
        editPremium.putBoolean(Const.PREMIUM_KEY, flag)
        editPremium.apply()
    }



    private val prefNotebook: SharedPreferences =
        context.getSharedPreferences("TABLE", Context.MODE_PRIVATE)
    private val editNotebook: SharedPreferences.Editor = prefNotebook.edit()

    fun getStringNoteBook(): String? {
        return prefNotebook.getString(Const.KEY_NOTE_BOOK, greetings)
    }

    fun saveStringNoteBook(value: String) {
        editNotebook.putString(Const.KEY_NOTE_BOOK, value)
        editNotebook.apply()
    }


}