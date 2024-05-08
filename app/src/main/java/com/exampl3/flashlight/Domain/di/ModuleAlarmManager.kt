package com.exampl3.flashlight.Domain.di

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleAlarmManager {

    @Provides
    @Singleton
    fun providesAlarmManager(context: Application): AlarmManager{
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}