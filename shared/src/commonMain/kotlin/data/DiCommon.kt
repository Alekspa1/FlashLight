package data

import MainViewModel
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.perository.AlarmRepeadImp
import data.perository.SaveDeleteImageImpl
import data.room.myDataBase
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.SaveDeleteImageRepositpry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.math.sin


import domain.repostirory.SharedPrefRepository
import domain.repostirory.SettingsAppRepository
import data.perository.MultiplatrormSettings
import data.perository.MultiplatrormAppSettings
import org.koin.core.qualifier.named

expect val moduleAnotherPlatform: Module

val appModule = module {
    viewModelOf(::MainViewModel)

    single<myDataBase> {

        val builder: androidx.room.RoomDatabase.Builder<myDataBase> = get()

        builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single { get<myDataBase>().CourseDao() }
    
    single<SharedPrefRepository> { MultiplatrormSettings(settings = get(named("noteBook"))) }
    single<SettingsAppRepository > { MultiplatrormAppSettings(settings = get(named("settings"))) }
    
    single<AlarmRepeadRepository> { AlarmRepeadImp(get(),get()) }
    factory<SaveDeleteImageRepositpry> { SaveDeleteImageImpl(get()) }

}





fun initKoin(config: KoinAppDeclaration? = null) {

    startKoin {

        config?.invoke(this)

        modules(appModule, moduleAnotherPlatform)

    }

}
