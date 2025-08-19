package com.exampl3.flashlight.Domain

import android.app.AlarmManager
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.room.Room
import com.exampl3.flashlight.Data.InsertDateAndTimeImpl
import com.exampl3.flashlight.Data.ChangeAlarmImp
import com.exampl3.flashlight.Data.GetSystemSoundImp
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.repository.GetSystemSoundRepository
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.Domain.repository.InsertOrDeleteAlarmReository
import com.exampl3.flashlight.Domain.repository.ThemeRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Di {


    @Provides
    @Singleton
    fun provedesTimeRepository(): InsertDateAndTimeRepository {
        return InsertDateAndTimeImpl()
    }

    @Provides
    @Singleton
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @Singleton
    fun provideGetSystemSoundRepository(contentResolver: ContentResolver): GetSystemSoundRepository {
        return GetSystemSoundImp(contentResolver)
    }




    @Provides
    @Singleton
    fun provedesSettingsRepository(context: Application): SettingsSharedPreference {
        return SettingsSharedPreference(context)
    }

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provedesThemeRepository(context: Application, setting:SettingsSharedPreference ): ThemeRepository {
        return ThemeImp(context, setting)
    }

    @Provides
    @Singleton
    fun providesInsertOrDeletePerository(context: Application): InsertOrDeleteAlarmReository {
        return ChangeAlarmImp(context, providesAlarmManager(context))
    }

    @Provides
    @Singleton
    fun providesAlarmManager(context: Application): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provadeDB(context: Application): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java, "db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesVoiceIntent(): Intent {
        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        return voiceIntent
    }


}