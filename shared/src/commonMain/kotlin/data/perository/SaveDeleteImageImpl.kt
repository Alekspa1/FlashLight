package data.perository

import domain.repostirory.PathProviderRepostitory
import domain.repostirory.SaveDeleteImageRepositpry

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

class SaveDeleteImageImpl(val pathProvider : PathProviderRepostitory) : SaveDeleteImageRepositpry {

    override fun save(temporaryPathString: String, fileName: String) {
        val sourcePath = temporaryPathString.toPath()
        val targetFullPath = getUri(fileName).toPath()

        // На всякий случай создаем папку /images на ПК, если её ещё нет
        targetFullPath.parent?.let { parentPath ->
            FileSystem.SYSTEM.createDirectories(parentPath)
        }

        FileSystem.SYSTEM.copy(sourcePath, targetFullPath)
    }

    override fun delete(fileName: String) {
        if (fileName.isNotEmpty()) {
            // 1. Склеиваем актуальное начало и имя файла, превращая в Path для Okio
            val fileToDeletePath = getUri(fileName).toPath()

            // 2. Стираем файл с диска устройства
            FileSystem.SYSTEM.delete(fileToDeletePath)
        }
    }

    override fun getUri(fileName: String): String {
        if (fileName.isEmpty()) return ""
        return "${pathProvider.getInternalAppPath()}/images//$fileName"
    }
}