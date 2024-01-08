package com.exampl3.flashlight.Domain.Room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Item::class], version = 1)
abstract class GfgDatabase : RoomDatabase() {
   abstract fun CourseDao(): CourseDao

}

