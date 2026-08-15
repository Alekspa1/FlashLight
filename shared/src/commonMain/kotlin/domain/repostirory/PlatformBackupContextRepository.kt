package domain.repostirory

import okio.Source
import okio.Sink
import io.github.vinceglb.filekit.core.PlatformFile

interface PlatformBackupContextRepository {
    // Для экспорта: берем локальный файл приложения и пишем в файл пользователя
    suspend fun exportDatabase(targetPlatformFile: PlatformFile): Boolean
    suspend fun importDatabase(sourcePlatformFile: PlatformFile): Boolean
}
