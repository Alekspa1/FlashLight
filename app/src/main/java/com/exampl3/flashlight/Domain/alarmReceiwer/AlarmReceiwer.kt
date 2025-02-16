package com.exampl3.flashlight.Domain.alarmReceiwer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.TEN_MINUTES
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.InsertDateAndAlarm
import com.exampl3.flashlight.Domain.insertOrDeleteAlarm.ChangeAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiwer : BroadcastReceiver() {
    @Inject
    lateinit var db: Database

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    @Inject
    lateinit var notificationBuilderPassed: NotificationBuilderPassed

    @Inject
    lateinit var insertDateAndAlarm: InsertDateAndAlarm

    @Inject
    lateinit var changeAlarm: ChangeAlarmUseCase

    private lateinit var calendarZero: Calendar


    override fun onReceive(context: Context, intent: Intent) {

        calendarZero = Calendar.getInstance()

        when (intent.action) {

            Const.keyIntentAlarm -> {
                val item = intent.getSerializableExtra(Const.keyIntent) as Item
                notificationBuilder.input(item)
                repeatAlarm(item, "", context)

            } // Приход будильника

            Const.keyIntentCallBackReady -> {
                val item = intent.getSerializableExtra(Const.keyIntentCallBackReady) as Item
                when (item.interval) {
                    Const.alarmOne -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.CourseDao()
                                .updateItem(item.copy(change = true, changeAlarm = false))
                        }
                    }
                }
                notificationBuilder.alarmPush().cancel(item.id!!)

            } // Когда нажал кнопку готово

            Const.keyIntentCallBackPostpone -> {
                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = intent.getSerializableExtra(Const.keyIntentCallBackPostpone) as Item
                when (item.interval) {
                    Const.alarmOne -> {
                        insertDateAndAlarm.ChangeItemBeforeAlarm(item, context, time)
//                        changeAlarm.exum(item.copy(alarmTime = time), item.interval)
//                        CoroutineScope(Dispatchers.IO).launch {
//                            db.CourseDao().updateItem(item.copy(alarmTime = time))
//                        }

                        //insertAlarm.insertAlarm(item, Const.alarmOne, "", time)
                    }

                    else -> {
                        val newItemFals = item.copy(
                            id = item.id?.plus(1000),
                            interval = Const.alarmRepeat,
                            alarmTime = time
                        )
                        changeAlarm.exum(newItemFals, Const.alarmOne)
                        //alarmManager.alarmInsert(newItemFals, Const.alarmOne)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
                notificationBuilder.alarmPush().cancel(item.id!!)
            } // Когда нажал кнопку отложить

            Const.reboot -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            changeAlarm.exum(item, item.interval)
                            //alarmManager.alarmInsert(item, item.interval)
                        }
                        if (item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                            notificationBuilderPassed.input(item)
                            repeatAlarm(item, "(Пропущено)", context)

                        }
                    }
                }

            } // После перезагрузки
        }
    }

    private fun repeatAlarm(item: Item, value: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            when (item.interval) {
                Const.alarmOne -> {
                    db.CourseDao().updateItem(
                        item.copy(
                            changeAlarm = false,
                            name = "${item.name} $value".trim()
                        )
                    )
                }

                else -> {
                    db.CourseDao().updateItem(item.copy(changeAlarm = false))
                    insertDateAndAlarm.alarmRepead(item, context)
                }

            }
        }

    } // Установка повторяющихся будильников


}

