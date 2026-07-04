package data

import com.dragon.shared.data.repostitory.DesktopSharedPrefImpl
import domain.repostirory.SharedPrefRepository
import org.koin.core.module.Module
import org.koin.dsl.module

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dragon.shared.data.repostitory.DesktopAlarmImpl
import com.dragon.shared.data.repostitory.DesktopDeleteImageInItemImpl
import com.dragon.shared.data.repostitory.DesktopPermissonImp
import data.perository.AlarmRepeadImp
import java.io.File
import data.room.myDataBase
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.DeleteImageInItemReository
import domain.repostirory.PermissionRepository

actual val moduleAnotherPlatform: Module = module {
    single<SharedPrefRepository> { DesktopSharedPrefImpl() }

    single<RoomDatabase.Builder<myDataBase>> {
        // Указываем путь к файлу на жестком диске ПК
        val dbFile = File(System.getProperty("user.home"), ".focus_app/focus_database.db")

        // Создаем и отдаем чистый строитель Room для Windows!
        Room.databaseBuilder<myDataBase>(
            name = dbFile.absolutePath
        )
    }
    single<DeleteImageInItemReository> { DesktopDeleteImageInItemImpl() }
    single<PermissionRepository> { DesktopPermissonImp() }
    single<AlarmRepository> { DesktopAlarmImpl(db = get()) }
}