package data

import data.repository.IosSharedPrefImpl
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val moduleSharedPref: Module = module {
    single <SharedPrefRepository> { IosSharedPrefImpl() }

}
