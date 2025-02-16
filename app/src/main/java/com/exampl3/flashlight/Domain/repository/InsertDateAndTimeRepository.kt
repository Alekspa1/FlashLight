package com.exampl3.flashlight.Domain.repository

import android.content.Context
import com.exampl3.flashlight.Data.Room.Item
import java.util.Calendar

interface InsertDateAndTimeRepository {

    suspend fun insertDate(item: Item, context: Context) : Calendar

    suspend fun insertTime(item: Item, date: Calendar?, context: Context) : Long

    suspend fun insertActionByItem(context: Context) : Int

    fun proverkaFreeAndinsertStringInterval(
        item: Item,
        context: Context,
        action: Int,
        date: Long,
        premium: Boolean
    ) : Item?
    fun createItem(item: Item, interval: Int, intervalText: String, alareTime: Long): Item


}