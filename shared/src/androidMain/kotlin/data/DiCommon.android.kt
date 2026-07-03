package data

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import data.repostitory.AndroidSharedPrefIml
import data.room.myDataBase
import domain.repostirory.SharedPrefRepository
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.alarmReceiwer.NotificationBuilder
import data.alarmReceiwer.NotificationBuilderPassed
import data.repostitory.AndroidAlarmImpl
import data.repostitory.AnroidDeleteImageInItemImpl
import data.repostitory.AndroidPermissionImpl
import domain.repostirory.AlarmRepository
import domain.repostirory.DeleteImageInItemReository
import domain.repostirory.PermissionRepository
import org.koin.dsl.bind
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

    single<DeleteImageInItemReository> { AnroidDeleteImageInItemImpl()}
    single { AndroidPermissionImpl(get()) } bind PermissionRepository::class

    single<AlarmManager> {
        val context: Context = get()
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    single<AlarmRepository> { AndroidAlarmImpl( get(),get()) }

    factory<NotificationBuilder> { NotificationBuilder(get()) }
    factory<NotificationBuilderPassed> { NotificationBuilderPassed(get()) }

}
