package com.exampl3.flashlight.Domain.model.di

import android.app.Application
import androidx.room.Room
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleRoom {

    @Provides
    @Singleton
    fun provadeDB(context: Application): GfgDatabase {
    return Room.databaseBuilder(
        context,
        GfgDatabase::class.java, "db"
    ).build()
    }
}