package data

import com.dragon.shared.data.repostitory.DesktopSharedPrefImpl
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import data.room.myDataBase

actual val moduleSharedPref: Module = module {
    single<SharedPrefRepository> { DesktopSharedPrefImpl() }
}

actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<myDataBase>> {
        // Указываем путь к файлу на жестком диске ПК
        val dbFile = File(System.getProperty("user.home"), ".focus_app/focus_database.db")

        // Создаем и отдаем чистый строитель Room для Windows!
        Room.databaseBuilder<myDataBase>(
            name = dbFile.absolutePath
        )
    }
}