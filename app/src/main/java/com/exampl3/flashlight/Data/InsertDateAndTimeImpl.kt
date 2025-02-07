package com.exampl3.flashlight.Data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item

import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.Presentation.DialogItemList

import kotlinx.coroutines.CompletableDeferred
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Locale

import javax.inject.Singleton



class InsertDateAndTimeImpl : InsertDateAndTimeRepository {
    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var timePickerDialog: TimePickerDialog


    override suspend fun insertDate(context: Context): Calendar {
        val deferredDate = CompletableDeferred<Calendar>()
        calendar = Calendar.getInstance()
        calendarZero = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                deferredDate.complete(calendar)
            },
            calendarZero.get(Calendar.YEAR),
            calendarZero.get(Calendar.MONTH),
            calendarZero.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

        return deferredDate.await()

    }


    override suspend fun insertTime(item: Item, context: Context): Calendar {
        val timeDeferred = CompletableDeferred<Calendar>()
        calendar = Calendar.getInstance()
        calendarZero = Calendar.getInstance()
        timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                timeDeferred.complete(calendar)
            },
            calendarZero.get(Calendar.HOUR_OF_DAY),
            calendarZero.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()

        return timeDeferred.await()
    }

    override suspend fun insertDateAndTime(
        premium: Boolean,
        item: Item,
        date: Calendar,
        context: Context
    ) : Item {
        val itemDeferred = CompletableDeferred<Item>()
        val selectedTime = insertTime(item, context)
        date.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
        date.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
        if (proverkatime(date.timeInMillis)) {
            DialogItemList.insertAlarm(
                context,
                object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        proverkaFreeAndinsertStringInterval(
                            item,
                            premium,
                            context,
                            action,
                            date.timeInMillis
                        )?.let { itemDeferred.complete(it) }

                    }
                })
        } else {
            Toast.makeText(
                context,
                "Вы выбрали время которое уже прошло",
                Toast.LENGTH_SHORT
            ).show()
        }
        return itemDeferred.await()
    }

    override fun proverkatime(alarmTime: Long): Boolean {
        return alarmTime >= calendarZero.timeInMillis
    }

    override fun proverkaFreeAndinsertStringInterval(
        item: Item,
        premium: Boolean,
        context: Context,
        action: Int,
        date: Long
    ) : Item?{
         when (action) {
            Const.alarmOne -> { return createItem(item, action, "", date) }

            Const.alarmDay -> {
                if (premium) return  createItem(
                    item,
                    action,
                    "и через день",
                    date
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmWeek -> {
                if (premium) return createItem(
                    item,
                    action,
                    "и через неделю",
                    date
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmMonth -> {

                if (premium) return createItem(
                    item,
                    action,
                    "и через месяц",
                    date
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmYear -> {
                if (premium) return createItem(item, action, "и через год", date)
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        return null
    }

    override fun createItem(
        item: Item,
        interval: Int,
        intervalText: String,
        alareTime: Long
    ): Item {
        val dateFormat = "dd.MM.yyyy"
        val timeFormat = "HH:mm"
        val date = SimpleDateFormat(dateFormat, Locale.US)
        val time = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = date.format(alareTime)
        val resutTime = time.format(alareTime)
        val newAlarmText = "Напомнит: $resultDate в $resutTime"
        val newitem =
            item.copy(
                changeAlarm = true,
                alarmText = "$newAlarmText $intervalText",
                alarmTime = alareTime,
                change = false,
                name = item.name,
                interval = interval
            )
        return newitem
    }


}