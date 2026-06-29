package data

import MainViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


expect val moduleSharedPref: Module

val appModule = module {

    viewModelOf(::MainViewModel)

}



fun initKoin(config: KoinAppDeclaration? = null) {

    startKoin {

        config?.invoke(this)

        modules(appModule, moduleSharedPref)

    }

}