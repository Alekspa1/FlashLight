package com.exampl3.flashlight.Domain.insertDateAndTime

import android.content.Context
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertDateUseCase @Inject constructor(private val insertDate: InsertDateAndTimeRepository) {

    suspend fun exum(context: Context): Calendar {
        return insertDate.insertDate(context)
    }
}