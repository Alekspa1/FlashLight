package com.dragon.shared.data.repostitory

import domain.repostirory.PickerRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CompletableDeferred
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class DesktopPickerImpl(private val windowFrame: Frame? = null) : PickerRepository {


    override suspend fun openZipPicker(): PlatformFile? {
        val deferred = CompletableDeferred<PlatformFile?>()

        // Создаем нативный диалог выбора файлов операционной системы
        val dialog = FileDialog(windowFrame, "Выберите файл", FileDialog.LOAD).apply {
            // Выставляем фильтр в зависимости от того, что запросил экран
                file = "*.zip"
                title = "Выберите файл бэкапа (.zip)"

            isVisible = true // Блокирует поток, пока пользователь не выберет файл
        }

        val fileName = dialog.file
        val directory = dialog.directory

        if (fileName != null && directory != null) {
            val file = File(directory, fileName)
            // Оборачиваем стандартный java.io.File в PlatformFile от FileKit,
            // чтобы твоя ViewModel в commonMain получила привычный тип данных!
            deferred.complete(PlatformFile(file))
        } else {
            deferred.complete(null) // Юзер закрыл окно или нажал "Отмена"
        }

        return deferred.await()
    }

    override suspend fun createZipPicker(defaultName: String): PlatformFile? {
        val deferred = CompletableDeferred<PlatformFile?>()

        // Диалог СОХРАНЕНИЯ файла (Экспорт)
        val dialog = FileDialog(windowFrame, "Сохранить резервную копию как...", FileDialog.SAVE).apply {
            file = defaultName
            isVisible = true
        }

        val fileName = dialog.file
        val directory = dialog.directory

        if (fileName != null && directory != null) {
            val file = File(directory, fileName)
            deferred.complete(PlatformFile(file))
        } else {
            deferred.complete(null)
        }

        return deferred.await()
    }
}