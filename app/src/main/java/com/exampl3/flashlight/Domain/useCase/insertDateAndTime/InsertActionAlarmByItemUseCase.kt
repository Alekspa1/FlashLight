package com.exampl3.flashlight.Domain.useCase.insertDateAndTime

import android.content.Context
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertActionAlarmByItemUseCase @Inject constructor(private val action: InsertDateAndTimeRepository) {

    suspend fun exum(context: Context, premium: Boolean): Int? {
        return action.insertActionByItem(context, premium)

    }


}