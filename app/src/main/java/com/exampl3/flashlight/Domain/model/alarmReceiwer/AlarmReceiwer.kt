package com.exampl3.flashlight.Domain.model.alarmReceiwer

import android.app.AlarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.exampl3.flashlight.Domain.model.AlarmManagerImp
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.model.InsertTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiwer: BroadcastReceiver() {
    @Inject
    lateinit var db: GfgDatabase
    @Inject
    lateinit var alarmManager: AlarmManagerImp
    @Inject
    lateinit var notificationBuilder: NotificationBuilder
    @Inject
    lateinit var notificationBuilderPassed: NotificationBuilderPassed
    @Inject
    lateinit var insertTime: InsertTime

    private lateinit var calendarZero: Calendar

    override fun onReceive(context: Context, intent: Intent) {
        calendarZero = Calendar.getInstance()


        when (intent.action) {
            Const.keyIntentAlarm -> {
                val item = intent.getSerializableExtra(Const.keyIntent) as Item
                notificationBuilder.input(item)
                repeatAlarm(item, "")

            } // Приход будильника

            Const.keyIntentCallBackReady -> {
                val item = intent.getSerializableExtra(Const.keyIntentCallBackReady) as Item
                when(item.interval){
                    Const.alarmOne->{
                        CoroutineScope(Dispatchers.IO).launch {
                            db.CourseDao()
                                .update(item.copy(change = true, changeAlarm = false))
                        }
                    }
                }
               notificationBuilder.alarmPush().cancel(item.id!!)

            } // Когда нажал кнопку готово

            Const.keyIntentCallBackPostpone -> {
                val time = calendarZero.timeInMillis + 600000
                val item = intent.getSerializableExtra(Const.keyIntentCallBackPostpone) as Item
                when(item.interval){
                    Const.alarmOne->{
                       insertTime.insertAlarm(item, Const.alarmOne,"", time)
                    } else->{
                        val newItemFals = item.copy(id = item.id?.plus(1000), interval = Const.alarmRepeat, alarmTime = time)
                    alarmManager.alarmInsert(newItemFals, Const.alarmOne)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
                notificationBuilder.alarmPush().cancel(item.id!!)
            } // Когда нажал кнопку отложить

            Const.reboot -> {
                Thread {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            alarmManager.alarmInsert(item, item.interval)
                        }
                        if (item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                            notificationBuilderPassed.input(item)
                            repeatAlarm(item,"(Пропущено)")

                        }
                    }
                }.start()
            } // После перезагрузки
        }
    }

    private fun repeatAlarm(item: Item, name: String){
        when(item.interval){
            Const.alarmOne-> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().update(
                        item.copy(
                            changeAlarm = false,
                            name = "${item.name} $name".trim()
                        )
                    )
                }
            }
            Const.alarmDay-> {
                insertTime.insertAlarm(item,item.interval,"и через день",  item.alarmTime+AlarmManager.INTERVAL_DAY)
            }
            Const.alarmWeek-> {
                insertTime.insertAlarm(item,item.interval,"и через неделю", item.alarmTime+AlarmManager.INTERVAL_DAY*7)
            }
            Const.alarmMonth-> {
                insertTime.insertAlarm(item,item.interval,"и через месяц", item.alarmTime+ Const.MONTH)
            }
        }
    }
}
