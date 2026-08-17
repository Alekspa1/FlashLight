package domain.repostirory

import io.github.vinceglb.filekit.core.PlatformFile

interface PickerRepository {

    // Шторка выбора ZIP-файла для восстановления (Импорт)
   suspend fun openZipPicker() : PlatformFile?

    // Шторка выбора места для сохранения нового файла бэкапа (Экспорт)
   suspend fun createZipPicker(defaultName: String) : PlatformFile?


}