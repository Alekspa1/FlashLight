package com.exampl3.flashlight.Domain.insertDateAndTime

import android.content.Context
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertDateAndTimeUseCase @Inject constructor(private val insertDateAndTime: InsertDateAndTimeRepository) {
    suspend fun exum(premium: Boolean, item: Item, date: Calendar, context: Context): Item {
        return insertDateAndTime.insertDateAndTime(premium, item, date, context)
    }
}