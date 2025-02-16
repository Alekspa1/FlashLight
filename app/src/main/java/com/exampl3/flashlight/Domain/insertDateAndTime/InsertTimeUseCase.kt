package com.exampl3.flashlight.Domain.insertDateAndTime

import android.content.Context
import android.widget.Toast
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.R
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InsertTimeUseCase @Inject constructor(private val insertTime: InsertDateAndTimeRepository) {

    private val calendarZero = Calendar.getInstance().timeInMillis

    suspend fun exum(item: Item, dateCalendar: Calendar?, context: Context): Long? {
        val time = insertTime.insertTime(item, dateCalendar, context)
        return if (time >= calendarZero) time
        else {
            Toast.makeText(context, R.string.timeHasPassed, Toast.LENGTH_SHORT).show()
            null
        }
    }
}