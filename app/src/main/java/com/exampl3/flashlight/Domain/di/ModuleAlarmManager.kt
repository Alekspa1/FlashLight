package com.exampl3.flashlight.Domain.di

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Calendar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleAlarmManager {

    @Provides
    @Singleton
    fun providesAlarmManager(context: Application): AlarmManager{
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }


    @Provides
    @Singleton
    fun providesCalendar(): Calendar = Calendar.getInstance()
}