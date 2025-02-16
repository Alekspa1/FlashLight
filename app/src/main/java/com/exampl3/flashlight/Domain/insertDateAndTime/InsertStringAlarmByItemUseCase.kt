package com.exampl3.flashlight.Domain.insertDateAndTime

import android.content.Context
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.insertOrDeleteAlarm.ChangeAlarmUseCase
import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.Domain.repository.InsertOrDeleteAlarmReository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertStringAlarmByItemUseCase @Inject constructor(private val proverka: InsertDateAndTimeRepository, private val changeAlarm: ChangeAlarmUseCase) {

    fun exum(item: Item, contex: Context, action: Int, date: Long,premium: Boolean) : Item? {
        return proverka.proverkaFreeAndinsertStringInterval(item,contex,action,date, premium)
    }


}