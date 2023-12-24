package com.exampl3.flashlight.Domain.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.exampl3.flashlight.Domain.Item

@Dao
interface UserDao {
    @Insert
    fun insert(user: Item)
    @Update
     fun update(user: Item)

    @Delete
    fun delete(user: Item)

    @Query("SELECT * FROM item")
    fun getAllUsers(): List<Item>
}
