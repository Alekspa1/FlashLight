package data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.room.model.Item
import data.room.model.ListCategory
import data.room.model.SubItem
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    //ITEM
    @Query("SELECT * FROM Item")
     fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM Item")
    suspend  fun getAllItemsNotFlow(): List<Item>
    
    @Query("SELECT * FROM Item ORDER BY sort ASC LIMIT 1")
    suspend fun getItemWithMinSort(): Item?

    @Query("SELECT * FROM Item WHERE id = :id LIMIT 1")
    suspend fun getItemFromId(id: Int): Item

    @Query("SELECT name FROM ListCategory")
    suspend fun getAllCategories(): List<String>

    @Query("UPDATE Item SET category = :newName WHERE category = :oldName")
    suspend fun updateAllitemInCategory(newName: String, oldName: String)

    @Query("SELECT * FROM Item WHERE alarmTime > :time and changeAlarm = 1 ")
    suspend fun getUpdateItemRestartPhone(time: Long): List<Item>

    @Query("SELECT * FROM Item")
    suspend fun getAllList(): List<Item>

    @Query("SELECT * FROM Item WHERE alarmTime >= :time and alarmTime < (:time+86400000) and (item.changeAlarm = 1 or item.change = 0)")
    fun getAllListCalendarRcView(time: Long): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE item.changeAlarm = 1 or item.change = 0")
    fun getItemsInCalendar() : Flow<List<Item>>

    @Query("DELETE FROM Item WHERE category == :value")
    suspend fun deleteItemInCategory(value: String)

    @Update
    suspend fun updateItem(item: Item)

    @Update
    suspend fun updateItemsOrder(items: List<Item>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(item: List<Item>)

    @Update
    suspend fun updateItems(items: List<Item>)

    @Query("SELECT * FROM Item WHERE changeAlarm = true")
    suspend fun getActiveAlarms(): List<Item>

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM Item")
    suspend fun deleteAllItems()


    //MENU


    @Query("SELECT COUNT(*) FROM ListCategory WHERE name = :name")
    suspend fun isCategoryExists(name: String): Int

    @Query("SELECT * FROM ListCategory")
    fun getAllListCategory(): Flow<List<ListCategory>>

    @Query("SELECT * FROM ListCategory")
  suspend  fun getAllListCategoryNotFlow(): List<ListCategory>

    @Insert
   suspend fun insertCategory(Courses: ListCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorys(categorys: List<ListCategory>)



    @Query("DELETE FROM ListCategory")
    suspend fun deleteAllCategorys()

    @Delete
    suspend fun deleteCategoryMenu(Course: ListCategory)

    @Update
    suspend fun updateCategory(Course: ListCategory)

    // SubItem
    
    @Query("SELECT * FROM sub_items")
    fun getAllSubItems(): Flow<List<SubItem>>

    @Query("SELECT * FROM sub_items")
    suspend fun getAllSubItemsNotFlow(): List<SubItem>

    @Query("SELECT * FROM sub_items WHERE idTask = :taskId ORDER BY sort ASC")
    fun getSubItemsForTask(taskId: Int): Flow<List<SubItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubItems(subItems: List<SubItem>)

    @Update
    suspend fun updateSubItem(subItem: SubItem)

    @Delete
    suspend fun deleteSubItem(subItem: SubItem)

    @Query("DELETE FROM sub_items")
    suspend fun deleteAllSubItems()

    @Query("DELETE FROM sub_items WHERE idTask = :taskId")
    suspend fun deleteAllSubItemsForTask(taskId: Int)

    @Query("UPDATE sub_items SET change = 1 WHERE idTask = :taskId AND change = 0")
    suspend fun markAllSubItemsAsCompleted(taskId: Int)




}
