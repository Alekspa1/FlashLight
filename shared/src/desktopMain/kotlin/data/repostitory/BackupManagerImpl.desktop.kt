package data.perository

import io.github.vinceglb.filekit.core.PlatformFile
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

actual fun zipDirectory(sourceDir: String, targetZipFile: String) {
    val sourceFile = File(sourceDir)
    val zipFile = File(targetZipFile)

    // Создаем поток записи ZIP
    ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
        // Рекурсивно пробегаем по всем файлам папки бэкапа
        sourceFile.walkTopDown().forEach { file ->
            if (file.isFile) {
                // Вычисляем относительный путь внутри архива (чтобы не сохранять полный путь ПК)
                val relativePath = file.relativeTo(sourceFile).path.replace('\\', '/')
                val zipEntry = ZipEntry(relativePath)

                zos.putNextEntry(zipEntry)
                FileInputStream(file).use { fis ->
                    fis.copyTo(zos)
                }
                zos.closeEntry()
            }
        }
    }
}

// 2. РАСПАКОВКА АРХИВА (ИМПОРТ)
actual fun unzipDirectory(sourceZipFile: String, targetDir: String) {
    val zipFile = File(sourceZipFile)
    val destDir = File(targetDir)

    // Создаем целевую папку, если её нет
    if (!destDir.exists()) destDir.mkdirs()

    ZipInputStream(FileInputStream(zipFile)).use { zis ->
        var entry = zis.nextEntry
        while (entry != null) {
            val newFile = File(destDir, entry.name)

            // Если внутри архива была подпапка (например, images/), создаем её на диске
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile?.mkdirs()
                // Записываем файл из архива на диск ПК
                FileOutputStream(newFile).use { fos ->
                    zis.copyTo(fos)
                }
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}

actual fun writeZipToPlatformFile(sourceFilePath: String, targetPlatformFile: PlatformFile): Boolean {
    return try {
        val targetPathString = targetPlatformFile.path ?: ""
        if (targetPathString.isNotEmpty()) {
            FileSystem.SYSTEM.copy(sourceFilePath.toPath(), targetPathString.toPath())
            true
        } else false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

actual fun readZipFromPlatformFile(sourcePlatformFile: PlatformFile, targetFilePath: String): Boolean {
    return try {
        val sourcePathString = sourcePlatformFile.path ?: ""
        if (sourcePathString.isNotEmpty()) {
            FileSystem.SYSTEM.copy(sourcePathString.toPath(), targetFilePath.toPath())
            true
        } else false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}