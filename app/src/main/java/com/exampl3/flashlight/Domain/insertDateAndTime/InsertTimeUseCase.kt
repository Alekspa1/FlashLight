package com.exampl3.flashlight.Domain.insertDateAndTime

import android.content.Context
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InsertTimeUseCase @Inject constructor(private val insertTime: InsertDateAndTimeRepository) {

    suspend fun exum(item: Item, context: Context): Calendar {
        return insertTime.insertTime(item,context)
    }
}