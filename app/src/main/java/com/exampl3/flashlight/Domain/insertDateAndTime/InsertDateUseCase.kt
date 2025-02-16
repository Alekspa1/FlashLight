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
class InsertDateUseCase @Inject constructor(private val insertDate: InsertDateAndTimeRepository) {

    private val calendarZero = Calendar.getInstance()

    suspend fun exum(item: Item,context: Context): Calendar? {
        val date = insertDate.insertDate(item, context)
        return if (date >= calendarZero) date
        else {
            Toast.makeText(context, R.string.timeHasPassed, Toast.LENGTH_SHORT).show()
            null
        }
    }
}