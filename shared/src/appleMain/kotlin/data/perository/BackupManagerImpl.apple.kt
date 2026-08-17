package data.perository

import io.github.vinceglb.filekit.core.PlatformFile
import okio.FileSystem
import okio.Path.Companion.toPath

actual fun zipDirectory(sourceDir: String, targetZipFile: String) {
}

actual fun unzipDirectory(sourceZipFile: String, targetDir: String) {
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