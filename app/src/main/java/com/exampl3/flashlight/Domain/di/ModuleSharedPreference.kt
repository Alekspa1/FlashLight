package com.exampl3.flashlight.Domain.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ModuleSharedPreference {

    @Provides
    fun providesSharedPreference(context: Application): SharedPreferences {
        return context.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
    }
}