package com.exampl3.flashlight.Domain.Alarm

import android.app.AlarmManager
import android.content.Context

class AlarmManagerDelete(private val alarmManagerRepository: AlarmManagerRepository) {
    fun alarmManagerDelete(id: Int, context: Context,alarmManager: AlarmManager){
        alarmManagerRepository.alarmManagerDelete(id, context,alarmManager)

    }
}