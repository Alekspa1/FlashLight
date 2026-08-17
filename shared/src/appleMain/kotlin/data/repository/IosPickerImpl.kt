package data.repository

import domain.repostirory.PickerRepository
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile

class IosPickerImpl() : PickerRepository {

    override suspend fun openZipPicker(): PlatformFile? {
        val pickerType = PickerType.File(extensions = listOf("zip"))


        return FileKit.pickFile(
            type = pickerType,
            // Используем правильное имя из FileKit — PickerMode.Single
            mode = io.github.vinceglb.filekit.core.PickerMode.Single
        )
    }

    override suspend fun createZipPicker(defaultName: String): PlatformFile? {

        return FileKit.saveFile(
            baseName = defaultName,
            extension = "zip",
        )
    }
}