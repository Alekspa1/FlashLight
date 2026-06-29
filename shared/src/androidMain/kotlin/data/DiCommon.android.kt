package data

import data.repostitory.AndroidSharedPrefIml
import domain.repostirory.SharedPrefRepository

import org.koin.dsl.module


actual val moduleSharedPref = module {

    single<SharedPrefRepository> { AndroidSharedPrefIml(get()) }
}