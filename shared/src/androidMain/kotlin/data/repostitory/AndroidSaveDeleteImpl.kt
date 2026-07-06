package data.repostitory

import android.content.Context
import android.net.Uri
import domain.repostirory.SaveDeleteImageRepositpry
import java.io.File
import java.io.FileNotFoundException

class AndroidSaveDeleteImpl(private val context: Context) : SaveDeleteImageRepositpry {

    override fun save(temporaryPathString: String, fileName: String) {
        try {
            // Создаем директорию images внутри filesDir, как в твоем коде
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            // Файл создается строго с тем именем, которое мы передали
            val file = File(imagesDir, fileName)
            val uri = Uri.parse(temporaryPathString)

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (_: FileNotFoundException) {
            // Исключение пропущено, как у тебя
        }
    }

    override fun delete(fileName: String) {
        try {
            if (fileName.isEmpty()) return
            // Находим файл по имени в папке images и удаляем его
            val file = File(File(context.filesDir, "images"), fileName)
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {
            // Исключение пропущено, как у тебя
        }
    }

    override fun getUri(fileName: String): String {
        if (fileName.isEmpty()) return ""
        // Возвращает полный путь, который Coil на Android прочитает без лагов
        return File(File(context.filesDir, "images"), fileName).absolutePath
    }
}