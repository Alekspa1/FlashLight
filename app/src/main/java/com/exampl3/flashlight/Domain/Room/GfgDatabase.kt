package com.exampl3.flashlight.Domain.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.exampl3.flashlight.Domain.Item


@Database(entities = [Item::class], version = 1)
abstract class GfgDatabase : RoomDatabase() {
   abstract fun CourseDao(): CourseDao
   companion object{
      fun initDb(context: Context) : GfgDatabase{
         return Room.databaseBuilder(
            context,
            GfgDatabase::class.java, "db"
         ).build()
      }
   }
}

