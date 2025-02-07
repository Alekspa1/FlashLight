package com.exampl3.flashlight.Data

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.alarmReceiwer.AlarmReceiwer
import com.exampl3.flashlight.Domain.repository.InsertOrDeleteAlarmReository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeAlarmImp @Inject constructor(
    private val context: Application,
    private val alarmManager: AlarmManager
) : InsertOrDeleteAlarmReository {


    override fun changeAlarm(item: Item, action: Int) {
        val alarmtIntent = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.putExtra(Const.keyIntent, item)
            intent.setAction(Const.keyIntentAlarm)
            PendingIntent.getBroadcast(
                context,
                item.id!!,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        when (action) {
            Const.deleteAlarm -> alarmManager.cancel(alarmtIntent)
            else -> alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.alarmTime,
                alarmtIntent
            )

        }

    }


}