package com.exampl3.flashlight.Domain.Alarm

import android.app.AlarmManager
import android.content.Context
import com.exampl3.flashlight.Domain.Room.Item

class AlarmManagerInsert(private val alarmManagerRepository: AlarmManagerRepository) {
    fun alarmManagerInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager){
        alarmManagerRepository.alarmManagerInsert(item, time, context,alarmManager)
    }
}