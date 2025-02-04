package com.exampl3.flashlight.Domain.di

import android.app.Application
import androidx.room.Room
import com.exampl3.flashlight.Data.Room.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleRoom {

//    @Provides
//    @Singleton
//    fun provadeDB(context: Application): Database {
//    return Room.databaseBuilder(
//        context,
//        Database::class.java, "db"
//    ).build()
//    }
}