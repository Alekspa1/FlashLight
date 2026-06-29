package com.dragon.shared.data.repostitory

import domain.repostirory.SharedPrefRepository
import java.io.File

class DesktopSharedPrefImpl : SharedPrefRepository {

    private val workingDir = File(System.getProperty("user.home"), ".focus_app").apply {
        if (!exists()) mkdirs() // Создаем папку, если её нет
    }
    private val file = File(workingDir, "notebook.txt")

    private val greetings = "Приветсвую дорогой друг!"
    override fun saveTextNoteBook(value: String) {
        try {
            // Записываем весь текст в файл (старый текст автоматически заменяется)
            file.writeText(value, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadTextNoteBook(): String {
        return try {
            if (file.exists()) {
                file.readText(Charsets.UTF_8) // Читаем сохраненный текст
            } else {
                greetings // Если файла нет — отдаем приветствие
            }
        } catch (e: Exception) {
            e.printStackTrace()
            greetings
        }
    }
}
