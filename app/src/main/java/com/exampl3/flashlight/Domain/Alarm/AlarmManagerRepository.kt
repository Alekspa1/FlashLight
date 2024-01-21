package com.exampl3.flashlight.Domain.Alarm

import android.app.AlarmManager
import android.content.Context
import com.exampl3.flashlight.Domain.Room.Item

interface AlarmManagerRepository {
    fun alarmManagerInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager)

    fun alarmManagerDelete(id: Int, context: Context,alarmManager: AlarmManager)

}