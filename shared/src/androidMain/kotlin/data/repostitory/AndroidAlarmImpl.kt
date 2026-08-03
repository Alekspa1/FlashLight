package data.repostitory

import CommonConst.KEY_INTENT
import CommonConst.KEY_INTENT_ALARM
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import data.alarmReceiwer.AlarmReceiwer
import data.room.Item
import domain.repostirory.AlarmRepository
import kotlin.jvm.java

class AndroidAlarmImpl( private val context: Context,
                        private val alarmManager: AlarmManager) : AlarmRepository {


    override fun createAlarm(item: Item) {

        val alarmtIntent = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.putExtra(KEY_INTENT, item.id)
            intent.setAction(KEY_INTENT_ALARM)
            PendingIntent.getBroadcast(
                context,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val clockInfo = AlarmManager.AlarmClockInfo(item.alarmTime, alarmtIntent)

        // 2. Устанавливаем ультимативный точный будильник
        alarmManager.setAlarmClock(clockInfo, alarmtIntent)
    }

    override fun deleteAlarm(id: Int) {

        val alarmtIntent = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.putExtra(KEY_INTENT, id)
            intent.setAction(KEY_INTENT_ALARM)
            PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val alarmtIntentRepeat = Intent(context, AlarmReceiwer::class.java).let { intent ->
            intent.putExtra(KEY_INTENT, id*-1)
            intent.setAction(KEY_INTENT_ALARM)
            PendingIntent.getBroadcast(
                context,
                id*-1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.cancel(alarmtIntent)
        alarmManager.cancel(alarmtIntentRepeat)
    }
}