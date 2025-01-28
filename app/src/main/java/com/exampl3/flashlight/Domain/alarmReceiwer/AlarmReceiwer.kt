package com.exampl3.flashlight.Domain.alarmReceiwer

import android.app.AlarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.exampl3.flashlight.Domain.AlarmManagerImp
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.TEN_MINUTES
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.InsertAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiwer: BroadcastReceiver () {
    @Inject
    lateinit var db: Database
    @Inject
    lateinit var alarmManager: AlarmManagerImp
    @Inject
    lateinit var notificationBuilder: NotificationBuilder
    @Inject
    lateinit var notificationBuilderPassed: NotificationBuilderPassed
    @Inject
    lateinit var insertAlarm: InsertAlarm
    @Inject
    lateinit var calendarZero: Calendar

    override fun onReceive(context: Context, intent: Intent) {



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
                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = intent.getSerializableExtra(Const.keyIntentCallBackPostpone) as Item
                when(item.interval){
                    Const.alarmOne->{
                       insertAlarm.insertAlarm(item, Const.alarmOne,"", time)
                    } else->{
                        val newItemFals = item.copy(id = item.id?.plus(1000), interval = Const.alarmRepeat, alarmTime = time)
                    alarmManager.alarmInsert(newItemFals, Const.alarmOne)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
                notificationBuilder.alarmPush().cancel(item.id!!)
            } // Когда нажал кнопку отложить

            Const.reboot -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            alarmManager.alarmInsert(item, item.interval)
                        }
                        if (item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                            notificationBuilderPassed.input(item)
                            repeatAlarm(item,"(Пропущено)")

                        }
                    }
                }

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
                insertAlarm.insertAlarm(item,item.interval,"и через день",  item.alarmTime+AlarmManager.INTERVAL_DAY)
            }
            Const.alarmWeek-> {
                insertAlarm.insertAlarm(item,item.interval,"и через неделю", item.alarmTime+AlarmManager.INTERVAL_DAY*7)
            }
            Const.alarmMonth-> {
                insertAlarm.insertAlarm(item,item.interval,"и через месяц", item.alarmTime+ Const.MONTH)
            }
            Const.alarmYear-> {
                    insertAlarm.insertAlarm(item,item.interval,"и через год", addOneYear(item.alarmTime))

                }

            }
        }


    private fun addOneYear(dateInMillis: Long): Long {
        calendarZero.timeInMillis = dateInMillis
        calendarZero.add(Calendar.YEAR, 1) // Добавляем один год
        return calendarZero.timeInMillis
    }

}

