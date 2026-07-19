package data

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import data.room.myDataBase
import domain.repostirory.SharedPrefRepository
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.alarmReceiwer.NotificationBuilder
import data.alarmReceiwer.NotificationBuilderPassed
import data.repostitory.AndroidAlarmImpl
import data.repostitory.AndroidPathProviderImp
import data.repostitory.AnroidDeleteImageInItemImpl
import data.repostitory.AndroidPermissionImpl
import data.repostitory.AndroidSaveDeleteImpl
import domain.repostirory.AlarmRepository
import domain.repostirory.DeleteImageInItemReository
import domain.repostirory.SaveDeleteImageRepositpry
import domain.repostirory.PathProviderRepostitory
import domain.repostirory.PermissionRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import data.perository.SaveDeleteImageImpl
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named

actual val moduleAnotherPlatform = module {
      val context: Context = get()
  
      single<Settings>(named("noteBook")){
          
          val prefs = context.getSharedPreferences("TABLE", Context.MODE_PRIVATE)
          SharedPreferencesSettings(prefs)
          
      }

       single<Settings>(named("settings")){
         
          val prefsSettings = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
          SharedPreferencesSettings(prefsSettings)
          
      }
   
    single<myDataBase> {
        

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
       
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    single<AlarmRepository> { AndroidAlarmImpl( get(),get()) }
    factory<SaveDeleteImageRepositpry> { SaveDeleteImageImpl(get()) }

    factory<NotificationBuilder> { NotificationBuilder(get(),get()) }
    factory<NotificationBuilderPassed> { NotificationBuilderPassed(get()) }
    factory<PathProviderRepostitory> { AndroidPathProviderImp(get()) }
    factory<SaveDeleteImageRepositpry> { AndroidSaveDeleteImpl(get()) }

}
