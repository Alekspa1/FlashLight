package com.exampl3.flashlight.Domain.Room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Item::class, ListCategory::class],
    version = 7,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration (from = 3, to = 4),
        AutoMigration (from = 4, to = 5),
        AutoMigration (from = 5, to = 6),
        AutoMigration (from = 6, to = 7),
                     ]

)
abstract class GfgDatabase: RoomDatabase()
{

   abstract fun CourseDao(): CourseDao

}

