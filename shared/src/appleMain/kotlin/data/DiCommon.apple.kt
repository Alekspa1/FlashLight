package data

import androidx.room.RoomDatabase
import data.repository.IosSharedPrefImpl
import data.room.myDataBase
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent


actual val moduleSharedPref: Module = module {
    single <SharedPrefRepository> { IosSharedPrefImpl() }

}
@OptIn(ExperimentalForeignApi::class)
actual val platformDatabaseModule: Module = module {
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
}