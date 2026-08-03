package data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import data.repository.IosGetPlatformImpl
import data.repository.IosPermissionImpl
import data.room.myDataBase
import domain.repostirory.GetPlatrormRepository
import domain.repostirory.PermissionRepository
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent

@OptIn(ExperimentalForeignApi::class)
actual val moduleAnotherPlatform: Module = module {

    
    single<Settings>(named("noteBook")) { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<Settings>(named("settings")) { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }

    single<RoomDatabase.Builder<myDataBase>> {
        // 1. Находим путь к безопасной папке Документов внутри песочницы iOS
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )

        // 2. Формируем финальное нативное имя файла базы данных
        val dbFilePath = documentDirectory?.path?.let { path ->
            val nsString = path as platform.Foundation.NSString
            nsString.stringByAppendingPathComponent("focus_database.db")
        } ?: "focus_database.db" // Заглушка на случай сбоя

        // 3. Создаем и отдаем строитель Room для iOS!
        Room.databaseBuilder<myDataBase>(
            name = dbFilePath
        )
    }
    single <PermissionRepository> { IosPermissionImpl() }
    factory<GetPlatrormRepository> { IosGetPlatformImpl() }

}
