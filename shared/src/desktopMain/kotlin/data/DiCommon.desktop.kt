package data

import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dragon.shared.data.repostitory.DesktopAlarmImpl
import com.dragon.shared.data.repostitory.DesktopGetPlatformImpl
import com.dragon.shared.data.repostitory.DesktopPathProviderImpl
import com.dragon.shared.data.repostitory.DesktopPaySdkImpl
import com.dragon.shared.data.repostitory.DesktopPermissonImp
import com.dragon.shared.data.repostitory.DesktopPickerImpl
import data.perository.AlarmRepeadImp
import java.io.File
import data.room.myDataBase
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.PathProviderRepostitory
import domain.repostirory.PermissionRepository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.PreferencesSettings
import domain.repostirory.BackupManagerRepository
import domain.repostirory.GetPlatrormRepository
import domain.repostirory.PaySdkRepository
import domain.repostirory.PickerRepository
import java.util.prefs.Preferences
import org.koin.core.qualifier.named
actual val moduleAnotherPlatform: Module = module {


    single<Settings>(named("noteBook")) { PreferencesSettings(Preferences.userRoot().node("NotebookSettings")) }
    single<Settings>(named("settings")) { PreferencesSettings(Preferences.userRoot().node("AppSettings")) }

    single<RoomDatabase.Builder<myDataBase>> {
        // Указываем путь к файлу на жестком диске ПК
        val dbFile = File(System.getProperty("user.home"), ".focus_app/focus_database.db")

        // Создаем и отдаем чистый строитель Room для Windows!
        Room.databaseBuilder<myDataBase>(
            name = dbFile.absolutePath
        )
    }

    single<PermissionRepository> { DesktopPermissonImp() }
    single<AlarmRepository> { DesktopAlarmImpl(db = get()) }
    factory<PathProviderRepostitory> { DesktopPathProviderImpl() }
    factory<GetPlatrormRepository> { DesktopGetPlatformImpl() }
    factory<PaySdkRepository> { DesktopPaySdkImpl() }
    single<PickerRepository> { DesktopPickerImpl(windowFrame = null) }
}
