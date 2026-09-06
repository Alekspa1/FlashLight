package data.perository

import CommonConst.KEY_NOTE_BOOK
import CommonConst.PLATFORM_DESKTOP
import CommonConst.PREMIUM_KEY

import com.russhwolf.settings.Settings
import domain.repostirory.GetPlatrormRepository
import domain.repostirory.SharedPrefRepository

class MultiplatrormSettings(private val settings: Settings, private val platform : GetPlatrormRepository) : SharedPrefRepository{

  
  private val greetings = if(platform.getPlatform() == PLATFORM_DESKTOP) "Приветсвую дорогой друг"
    else

        "Дорогие пользователи! ❤️\n\nИз-за агрессивных систем энергосбережения на " +

                "некоторых моделях телефонов (особенно Huawei, Honor, Xiaomi) напоминания могут срабатывать с опозданием или сбрасываться системой при перезагрузке устройства." +

                "\n\nЧтобы всё работало секунда в секунду, перейдите в настройки разрешите ему:" +

                "\n∙ Работу в фоновом режиме\n∙ В настройках приложения найдите пункт который отключает энергосбережение или оптимизацию батареи(на каждом смартфоне по разному)\n\n⚠️" +

                " Если после перезагрузки смартфона напоминания не сработали, вам необходимо будет повторно входить в приложение после " +

                "каждой перезагрузки чтобы напоминания обновились" +

                "\n\nПо любым вопросам и предложениям пишите мне через кнопку обратной связи." +

                "Если вам понравится мое приложение, буду очень рад вашему хорошему отзыву! "
  

    override fun saveTextNoteBook(value: String) {
        settings.putString(KEY_NOTE_BOOK, value)

        

    }

    override fun loadTextNoteBook(): String = settings.getString(KEY_NOTE_BOOK, greetings)

    override fun savePremium(flag: Boolean) {
        settings.putBoolean(PREMIUM_KEY, flag)

    }

    override fun getPremium(): Boolean = settings.getBoolean(PREMIUM_KEY, false)


}
  
