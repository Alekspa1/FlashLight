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
        "Дорогие пользователи! ❤️\n\nИз-за агрессивных систем энергосбережения на " +
                "некоторых моделях телефонов (особенно Huawei, Honor, Xiaomi) напоминания могут срабатывать с опозданием или сбрасываться системой при перезагрузке устройства." +
                "\n\nЧтобы всё работало секунда в секунду, перейдите в Настройки приложения и разрешите ему:" +
                "\n• Работу в фоновом режиме\n• Отображение поверх других окон\n• Запись системных настроек\n\n⚠️" +
                " Если после перезагрузки смартфона напоминания не сработали, просто загляните в приложение — оно автоматически обновит и включит все ваши будильники." +
                "\n\nЕсли вам понравится мое приложение, буду очень рад вашему хорошему отзыву! " +
                "По любым вопросам и предложениям пишите мне через кнопку обратной связи. Спасибо, что выбрали моё приложение!"


    private var prefPremium: SharedPreferences =
        context.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
    private val editPremium: SharedPreferences.Editor = prefPremium.edit()

    fun getPremium(): Boolean = prefPremium.getBoolean(Const.PREMIUM_KEY, false)

    fun savePremium(flag: Boolean) {
        editPremium.putBoolean(Const.PREMIUM_KEY, flag)
        editPremium.apply()
    }

    fun isFirstAlarm(): Boolean = prefPremium.getBoolean(Const.FIRST_ALARM, true)

    fun saveFirstAlarm(flag: Boolean) {
        editPremium.putBoolean(Const.FIRST_ALARM, flag)
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