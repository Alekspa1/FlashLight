package com.exampl3.flashlight.Domain.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


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
    @Delete
    suspend fun deleteList(list: List<Item>)
    @Update
    fun update(Course: Item)
    @Insert
    fun insertAll(Courses: Item)



    //MENU
    @Query("SELECT * FROM ListCategory")
    fun getAllListCategory(): Flow<List<ListCategory>>
    @Query("SELECT * FROM ListCategory")
    suspend fun getAllListCategoryNoFlow(): List<ListCategory>
    @Insert
    fun insertCategory(Courses: ListCategory)
    @Delete
    fun delete(Course: Item)
    @Delete
    fun deleteCategoryMenu(Course: ListCategory)
    @Update
    fun updateCategory(Course: ListCategory)


}
