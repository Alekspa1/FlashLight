package domain.repostirory

import okio.Source
import okio.Sink
import io.github.vinceglb.filekit.core.PlatformFile

interface PlatformBackupContextRepository {
    // Открывает поток для ЗАПИСИ в файл, который пользователь выбрал для сохранения бэкапа
    fun getSinkFromPlatformFile(platformFile: PlatformFile): Sink

    // Открывает поток для ЧТЕНИЯ из файла архива, который пользователь выбрал для импорта
    fun getSourceFromPlatformFile(platformFile: PlatformFile): Source
}
