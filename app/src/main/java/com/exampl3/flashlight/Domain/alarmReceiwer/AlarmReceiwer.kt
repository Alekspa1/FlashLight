package com.exampl3.flashlight.Domain.alarmReceiwer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM_ONE
import com.exampl3.flashlight.Const.KEY_INTENT_ALARM
import com.exampl3.flashlight.Const.KEY_INTENT_CALL_BACKREADY
import com.exampl3.flashlight.Const.KEY_INTENT_CALL_POSTPONE
import com.exampl3.flashlight.Const.REBOOT
import com.exampl3.flashlight.Const.TEN_MINUTES
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.InsertDateAndAlarm
import com.exampl3.flashlight.Domain.useCase.insertOrDeleteAlarm.ChangeAlarmUseCase
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

            KEY_INTENT_ALARM -> {
                val item = intent.getSerializableExtra(Const.KEY_INTENT) as Item
                notificationBuilder.input(item)
                repeatAlarm(item, "", context)

            } // Приход будильника

            KEY_INTENT_CALL_BACKREADY -> {
                val item = intent.getSerializableExtra(KEY_INTENT_CALL_BACKREADY) as Item
                when (item.interval) {
                    ALARM_ONE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.CourseDao()
                                .updateItem(item.copy(change = true, changeAlarm = false))
                        }
                    }
                }
                notificationBuilder.alarmPush().cancel(item.id!!)

            } // Когда нажал кнопку готово

            KEY_INTENT_CALL_POSTPONE -> {
                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = intent.getSerializableExtra(KEY_INTENT_CALL_POSTPONE) as Item
                when (item.interval) {
                    ALARM_ONE -> {
                        val newItem = item.copy(changeAlarm = true, alarmTime = time)
                        CoroutineScope(Dispatchers.IO).launch {
                            db.CourseDao().updateItem(newItem)
                        }
                        changeAlarm.exum(newItem, ALARM_ONE)
                    }

                    else -> {
                        val newItemFals = item.copy(
                            id = item.id?.plus(1000),
                            interval = Const.ALARM_REPEAT,
                            alarmTime = time
                        )
                        changeAlarm.exum(newItemFals, ALARM_ONE)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
                notificationBuilder.alarmPush().cancel(item.id!!)
            } // Когда нажал кнопку отложить

            REBOOT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            changeAlarm.exum(item, item.interval)
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
                ALARM_ONE -> {
                    db.CourseDao().updateItem(
                        item.copy(
                            change = false,
                            changeAlarm = false,
                            name = "${item.name} $value".trim()
                        )
                    )
                }

                else -> {
                    insertDateAndAlarm.alarmRepead(item, context)
                }

            }
        }

    } // Установка повторяющихся будильников


}

