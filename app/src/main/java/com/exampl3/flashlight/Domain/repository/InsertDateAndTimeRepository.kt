package com.exampl3.flashlight.Domain.repository

import android.content.Context
import com.exampl3.flashlight.Data.Room.Item
import java.util.Calendar

interface InsertDateAndTimeRepository {

    suspend fun insertDate(context: Context): Calendar
    suspend fun insertTime(item: Item, context: Context): Calendar
    suspend fun insertDateAndTime(premium: Boolean, item: Item, date: Calendar, context: Context) : Item
    fun proverkatime(alarmTime: Long): Boolean
    fun proverkaFreeAndinsertStringInterval(
        item: Item,
        premium: Boolean,
        context: Context,
        action: Int,
        date: Long
    ) : Item?
    fun createItem(item: Item, interval: Int, intervalText: String, alareTime: Long): Item


}