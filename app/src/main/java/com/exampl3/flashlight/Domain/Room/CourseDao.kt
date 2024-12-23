package com.exampl3.flashlight.Domain.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    //ITEM
    @Query("SELECT * FROM Item")
    fun getAll(): Flow<List<Item>>
    @Query("SELECT * FROM Item WHERE category == :value")
    fun getAllNew(value: String): Flow<List<Item>>
    @Query("SELECT * FROM Item WHERE category == :value")
    suspend fun getAllNewNoFlow(value: String): List<Item>
    @Query("SELECT * FROM Item")
    suspend fun getAllList(): List<Item>
    @Query("SELECT * FROM Item WHERE alarmTime > :time and alarmTime < (:time+86400000)")
    fun getAllListCalendarRcView(time: Long): Flow<List<Item>>
    @Query("DELETE FROM Item WHERE category == :value")
    fun deleteCategory(value: String)
    @Update
    fun update(Course: Item)
    @Insert
    fun insertAll(Courses: Item)



    //MENU
    @Query("SELECT * FROM ListCategory")
    fun getAllListCategory(): Flow<List<ListCategory>>
    @Insert
    fun insertCategory(Courses: ListCategory)
    @Delete
    fun delete(Course: Item)
    @Delete
    fun deleteCategoryMenu(Course: ListCategory)
    @Update
    fun updateCategory(Course: ListCategory)


}
