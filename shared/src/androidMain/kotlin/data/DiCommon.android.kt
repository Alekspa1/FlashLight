package data

import data.repostitory.SharedPrefIml
import domain.repostirory.SharedPrefRepository

import org.koin.dsl.module


actual val moduleSharedPref = module {

    single<SharedPrefRepository> { SharedPrefIml(get()) }
}