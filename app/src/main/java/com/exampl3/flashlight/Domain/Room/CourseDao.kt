package com.exampl3.flashlight.Domain.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    @Query("SELECT * FROM Item")
    fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM Item")
    fun getAllList(): List<Item>

    @Insert
     fun insertAll(Courses: Item)

    @Delete
    fun delete(Course: Item)
    @Update
    fun update(Course: Item)


}