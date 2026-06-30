package data

import android.content.Context
import data.repostitory.AndroidSharedPrefIml
import data.room.myDataBase
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.repostitory.DeleteImageInItemImpl
import domain.repostirory.DeleteImageInItemReository
import org.koin.dsl.module


actual val moduleAnotherPlatform = module {

    single<SharedPrefRepository> { AndroidSharedPrefIml(get()) }

    single<myDataBase> {

        val context: Context = get()
        val dbFile = context.getDatabasePath("db")

        val builder = Room.databaseBuilder<myDataBase>(
            context = context,
            name = dbFile.absolutePath
        )

        builder
            .setDriver(BundledSQLiteDriver()) // Подключаем SQLite драйвер
            .build() // Создаем готовую базу данных!
    }

    factory<DeleteImageInItemReository> { DeleteImageInItemImpl()}
}