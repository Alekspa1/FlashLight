package data.perository

import android.content.Context
import io.github.vinceglb.filekit.core.PlatformFile
import org.koin.mp.KoinPlatformTools
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

actual fun zipDirectory(sourceDir: String, targetZipFile: String) {
    val sourceFolder = File(sourceDir)
    val zipFile = File(targetZipFile)
    zipFile.parentFile?.mkdirs()

    ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
        sourceFolder.walkTopDown().forEach { file ->
            if (file.isFile) {
                val relativePath = sourceFolder.toURI().relativize(file.toURI()).path
                zos.putNextEntry(ZipEntry(relativePath))
                FileInputStream(file).use { fis -> fis.copyTo(zos) }
                zos.closeEntry()
            }
        }
        zos.flush()
    }
}

actual fun unzipDirectory(sourceZipFile: String, targetDir: String) {
    val zipFile = File(sourceZipFile)
    val destDir = File(targetDir)
    destDir.mkdirs()

    ZipInputStream(FileInputStream(zipFile)).use { zipIn ->
        var entry = zipIn.nextEntry
        while (entry != null) {
            val newFile = File(destDir, entry.name)
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile?.mkdirs()
                FileOutputStream(newFile).use { fos -> zipIn.copyTo(fos) }
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
    }
}

actual fun writeZipToPlatformFile(sourceFilePath: String, targetPlatformFile: PlatformFile): Boolean {
    return try {
        val sourceFile = File(sourceFilePath)
        if (!sourceFile.exists()) return false

        // Достаем контекст приложения нативным андроид-способом
        val context = KoinPlatformTools.defaultContext().get().get<Context>()

        // Используем свойство .uri, которое идеально работает в твоем коде картинок!
        context.contentResolver.openOutputStream(targetPlatformFile.uri)?.use { outputStream ->
            FileInputStream(sourceFile).use { fis ->
                fis.copyTo(outputStream)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

actual fun readZipFromPlatformFile(sourcePlatformFile: PlatformFile, targetFilePath: String): Boolean {
    return try {
        val targetFile = File(targetFilePath)
        targetFile.parentFile?.mkdirs()

        val context = KoinPlatformTools.defaultContext().get().get<Context>()

        context.contentResolver.openInputStream(sourcePlatformFile.uri)?.use { inputStream ->
            FileOutputStream(targetFile).use { fos ->
                inputStream.copyTo(fos)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}