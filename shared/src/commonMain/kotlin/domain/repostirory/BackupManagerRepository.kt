package domain.repostirory

import io.github.vinceglb.filekit.core.PlatformFile

interface BackupManagerRepository {
    suspend fun saveDb(targetPlatformFile: PlatformFile): Boolean
    suspend fun loadDb(sourcePlatformFile: PlatformFile): Boolean
}