package data.repository

import domain.repostirory.SharedPrefRepository
import platform.Foundation.NSUserDefaults

class IosSharedPrefImpl : SharedPrefRepository {
    private val greetings = "Приветсвую дорогой друг!"

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveTextNoteBook(value: String) {
        defaults.setObject(value, CommonConst.KEY_NOTE_BOOK)
    }

    override fun loadTextNoteBook(): String {
        val result = defaults.stringForKey(CommonConst.KEY_NOTE_BOOK)

        // МАГИЯ ТУТ: Печатаем лог загрузки
        if (result != null) {
            println("Focus-KMP [iOS]: Успешно ЗАГРУЖЕН текст из NSUserDefaults! Длина: ${result.length} симв.")
        } else {
            println("Focus-KMP [iOS]: Хранилище пустое, отдаю приветственный текст по умолчанию.")
        }

        return result ?: greetings
    }
}
