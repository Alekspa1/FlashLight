package com.exampl3.flashlight.Data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.exampl3.flashlight.Domain.Alarm.AlarmManagerRepository
import com.exampl3.flashlight.Domain.AlarmReceiwer
import com.exampl3.flashlight.Domain.Room.Item

object AlarmManagerImp: AlarmManagerRepository {

    override fun alarmManagerInsert(item: Item, time: Long,context: Context,alarmManager: AlarmManager) {
        val alarmtIntent = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.putExtra(Const.keyIntent, item)
            intent.setAction(Const.keyIntentAlarm)
            PendingIntent.getBroadcast(context, item.id!!, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmtIntent)
    }

    override fun alarmManagerDelete(id: Int, context: Context,alarmManager: AlarmManager) {
        val alarmtIntent = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.setAction(Const.keyIntentAlarm)
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.cancel(alarmtIntent)
    }
}