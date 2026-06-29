package data.repostitory

import android.content.Context
import android.content.SharedPreferences
import domain.repostirory.SharedPrefRepository

class SharedPrefIml(private val context: Context) : SharedPrefRepository {

    private val greetings =

        "Дорогие пользователи! ❤️\n\nИз-за агрессивных систем энергосбережения на " +

                "некоторых моделях телефонов (особенно Huawei, Honor, Xiaomi) напоминания могут срабатывать с опозданием или сбрасываться системой при перезагрузке устройства." +

                "\n\nЧтобы всё работало секунда в секунду, перейдите в настройки разрешите ему:" +

                "\n∙ Работу в фоновом режиме\n∙ В настройках приложения найдите пункт который отключает энергосбережение или оптимизацию батареи(на каждом смартфоне по разному)\n\n⚠️" +

                " Если после перезагрузки смартфона напоминания не сработали, вам необходимо будет повторно входить в приложение после " +

                "каждой перезагрузки чтобы напоминания обновились" +

                "\n\nПо любым вопросам и предложениям пишите мне через кнопку обратной связи." +

                "Если вам понравится мое приложение, буду очень рад вашему хорошему отзыву! "


    private val prefNotebook: SharedPreferences =

        context.getSharedPreferences("TABLE", Context.MODE_PRIVATE)


    private val editNotebook: SharedPreferences.Editor = prefNotebook.edit()


    override fun saveTextNoteBook(value: String) {

        editNotebook.putString(CommonConst.KEY_NOTE_BOOK, value)

        editNotebook.apply()

    }


    override fun loadTextNoteBook(): String = prefNotebook.getString(CommonConst.KEY_NOTE_BOOK, greetings)!!
}