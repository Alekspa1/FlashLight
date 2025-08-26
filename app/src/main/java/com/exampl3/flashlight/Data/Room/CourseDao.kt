package com.exampl3.flashlight.Data.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    //ITEM
    @Query("SELECT * FROM Item")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT name FROM ListCategory")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT * FROM Item WHERE category == :value")
    suspend fun getAllNewNoFlow(value: String): List<Item>

    @Query("SELECT * FROM Item WHERE alarmTime > :time ")
    suspend fun getUpdateItemRestartPhone(time: Long): List<Item>

    @Query("SELECT * FROM Item")
    suspend fun getAllList(): List<Item>

    @Query("SELECT * FROM Item WHERE alarmTime >= :time and alarmTime < (:time+86400000)")
   suspend fun getAllListCalendarRcView(time: Long): List<Item>

    @Query("DELETE FROM Item WHERE category == :value")
    fun deleteItemInCategory(value: String)

    @Update
    suspend fun updateItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM Item WHERE sort = (SELECT MIN(sort) FROM Item)")
    suspend fun getItemWithMaxSort(): Item?

    @Update
    suspend fun updateItems(items: List<Item>)


    //MENU
    @Query("SELECT COUNT(*) FROM ListCategory WHERE name = :name")
    suspend fun isCategoryExists(name: String): Int

    @Query("SELECT * FROM ListCategory")
    fun getAllListCategory(): Flow<List<ListCategory>>

    @Insert
   suspend fun insertCategory(Courses: ListCategory)

    @Delete
   suspend fun delete(Course: Item)

    @Delete
    fun deleteCategoryMenu(Course: ListCategory)

    @Update
    suspend fun updateCategory(Course: ListCategory)


}
