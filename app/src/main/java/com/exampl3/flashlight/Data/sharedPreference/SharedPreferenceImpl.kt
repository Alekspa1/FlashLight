package com.exampl3.flashlight.Data.sharedPreference

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.LogText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceImpl @Inject constructor(
    context: Application
) {
    private val greetings =
        "Дорогие пользователи! ❤️\n\nИз-за агрессивных систем энергосбережения на " +
                "некоторых моделях телефонов (особенно Huawei, Honor, Xiaomi) напоминания могут срабатывать с опозданием или сбрасываться системой при перезагрузке устройства." +
                "\n\nЧтобы всё работало секунда в секунду, перейдите в настройки разрешите ему:" +
                "\n• Работу в фоновом режиме\n• В настройках приложения найдите пункт который отключает энергосбережение или оптимизацию батареи(на каждом смартфоне по разному)\n\n⚠️" +
                " Если после перезагрузки смартфона напоминания не сработали, вам необходимо будет повторно входить в приложение после " +
                "каждой перезагрузки чтобы напоминания обновились" +
                "\n\nПо любым вопросам и предложениям пишите мне через кнопку обратной связи." +
                "Если вам понравится мое приложение, буду очень рад вашему хорошему отзыву! "


    private var prefPremium: SharedPreferences =
        context.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
    private val editPremium: SharedPreferences.Editor = prefPremium.edit()

    fun getPremium(): Boolean = prefPremium.getBoolean(Const.PREMIUM_KEY, false)

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
