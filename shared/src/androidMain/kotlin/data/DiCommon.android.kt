package data

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import data.alarmReceiwer.NotificationBuilder
import data.alarmReceiwer.NotificationBuilderPassed
import data.repostitory.AndroidAlarmImpl
import data.repostitory.AndroidPathProviderImp
import data.repostitory.AndroidPermissionImpl
import data.repostitory.AndroidSaveDeleteImpl
import data.room.myDataBase
import domain.repostirory.AlarmRepository
import domain.repostirory.PathProviderRepostitory
import domain.repostirory.PermissionRepository
import domain.repostirory.SaveDeleteImageRepositpry
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val moduleAnotherPlatform = module {



      single<Settings>(named("noteBook")){
          
          val prefs = androidContext().getSharedPreferences("TABLE", Context.MODE_PRIVATE)
          SharedPreferencesSettings(prefs)
          
      }

       single<Settings>(named("settings")){
         
          val prefsSettings = androidContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
          SharedPreferencesSettings(prefsSettings)
          
      }
   
    single<myDataBase> {
        

        val dbFile = androidContext().getDatabasePath("db")

        val builder = Room.databaseBuilder<myDataBase>(
            context = androidContext(),
            name = dbFile.absolutePath
        )

        builder
            .setDriver(BundledSQLiteDriver()) // Подключаем SQLite драйвер
            .build() // Создаем готовую базу данных!
    }


    single { AndroidPermissionImpl(get()) } bind PermissionRepository::class

    single<AlarmManager> {

        androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    single<AlarmRepository> { AndroidAlarmImpl( get(),get()) }


    factory<NotificationBuilder> { NotificationBuilder(get(),get()) }
    factory<NotificationBuilderPassed> { NotificationBuilderPassed(get()) }
    factory<PathProviderRepostitory> { AndroidPathProviderImp(get()) }
    factory<SaveDeleteImageRepositpry> { AndroidSaveDeleteImpl(get()) }

}
