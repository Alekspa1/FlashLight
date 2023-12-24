package com.exampl3.flashlight.Domain.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.exampl3.flashlight.Domain.Item
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    @Query("SELECT * FROM Item")
    fun getAll(): Flow<List<Item>>

    @Insert
     fun insertAll(Courses: Item)

    @Delete
    suspend fun delete(Course: Item)

}
