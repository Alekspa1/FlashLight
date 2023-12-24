package com.exampl3.flashlight.Domain.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.exampl3.flashlight.Domain.Item


@Database(entities = [Item::class], version = 1)
abstract class GfgDatabase : RoomDatabase() {
   abstract fun CourseDao(): CourseDao
}
