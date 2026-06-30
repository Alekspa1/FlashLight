package data

import MainViewModel
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.room.myDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


expect val moduleSharedPref: Module
expect val platformDatabaseModule : Module

val appModule = module {
    viewModelOf(::MainViewModel)

    single<myDataBase> {
        // Запрашиваем платформенный строитель под новое имя:
        val builder: androidx.room.RoomDatabase.Builder<myDataBase> = get()

        builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<myDataBase>().CourseDao() }

}





fun initKoin(config: KoinAppDeclaration? = null) {

    startKoin {

        config?.invoke(this)

        modules(appModule, moduleSharedPref,platformDatabaseModule)

    }

}