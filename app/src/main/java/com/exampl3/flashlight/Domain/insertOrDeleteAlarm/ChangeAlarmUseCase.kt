package com.exampl3.flashlight.Domain.insertOrDeleteAlarm

import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.repository.InsertOrDeleteAlarmReository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeAlarmUseCase @Inject constructor(private val changeAlarm: InsertOrDeleteAlarmReository) {

    fun exum(item: Item, action: Int){
        changeAlarm.changeAlarm(item, action)
    }
}