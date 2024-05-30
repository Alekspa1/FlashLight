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

    @Query("SELECT * FROM Item WHERE category == :value")
    fun getAllNew(value: String): Flow<List<Item>>
    @Query("SELECT * FROM Item WHERE category == :value")
    suspend fun getAllNewNoFlow(value: String): List<Item>

    @Query("SELECT * FROM ListCategory")
    fun getAllListCategory(): Flow<List<ListCategory>>
    @Insert
    fun insertCategory(Courses: ListCategory)

    @Query("SELECT * FROM Item")
    suspend fun getAllList(): List<Item>
    @Query("SELECT * FROM Item WHERE alarmTime > :time and alarmTime < (:time+86400000)")
    fun getAllListCalendarRcView(time: Long): Flow<List<Item>>
    @Insert
     fun insertAll(Courses: Item)
    @Delete
    fun delete(Course: Item)
    @Delete
    fun deleteCategoryMenu(Course: ListCategory)
    @Query("DELETE FROM Item WHERE category == :value")
    fun deleteCategory(value: String)
    @Delete
    suspend fun deleteList(list: List<Item>)
    @Update
    fun update(Course: Item)
    @Update
    fun updateCategory(Course: ListCategory)


}
