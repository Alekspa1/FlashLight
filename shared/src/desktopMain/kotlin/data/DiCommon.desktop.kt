package data

import com.dragon.shared.data.repostitory.DesktopSharedPrefImpl
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val moduleSharedPref: Module = module {
    single<SharedPrefRepository> { DesktopSharedPrefImpl() }
}
