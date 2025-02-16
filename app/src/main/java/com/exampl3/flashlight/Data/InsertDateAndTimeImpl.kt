package com.exampl3.flashlight.Data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl

import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.R

import kotlinx.coroutines.CompletableDeferred
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

import javax.inject.Singleton


class InsertDateAndTimeImpl : InsertDateAndTimeRepository {
    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var timePickerDialog: TimePickerDialog


    override suspend fun insertDate(item: Item, context: Context): Calendar {
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


    override suspend fun insertTime(item: Item, date: Calendar?, context: Context): Long {
        val timeDeferred = CompletableDeferred<Calendar>()

        calendarZero = Calendar.getInstance()

        if (date != null) {
            timePickerDialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    date.set(Calendar.HOUR_OF_DAY, hour)
                    date.set(Calendar.MINUTE, minute)

                    timeDeferred.complete(date)

                },
                calendarZero.get(Calendar.HOUR_OF_DAY),
                calendarZero.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }



        return timeDeferred.await().timeInMillis
    }


    override suspend fun insertActionByItem(context: Context): Int {
        val actionDeferred = CompletableDeferred<Int>()
        DialogItemList.insertAlarm(context, object : DialogItemList.ActionInt {
            override fun onClick(action: Int) {
                actionDeferred.complete(action)
            }
        })
        return actionDeferred.await()
    }

//    suspend fun insertDateAndTime(
//        premium: Boolean,
//        item: Item,
//        date: Calendar,
//        context: Context
//    ) : Item {
//        val itemDeferred = CompletableDeferred<Item>()
//        val selectedTime = insertTime(item, context)
//        date.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
//        date.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
//        if (proverkatime(date.timeInMillis)) {
//            DialogItemList.insertAlarm(
//                context,
//                object : DialogItemList.ActionInt {
//                    override fun onClick(action: Int) {
//                        proverkaFreeAndinsertStringInterval(
//                            item,
//                            premium,
//                            context,
//                            action,
//                            date.timeInMillis
//                        )?.let { itemDeferred.complete(it) }
//
//                    }
//                })
//        } else {
//            Toast.makeText(
//                context,
//                "Вы выбрали время которое уже прошло",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        return itemDeferred.await()
//    }


    override fun proverkaFreeAndinsertStringInterval(
        item: Item,
        context: Context,
        action: Int,
        date: Long,
        premium: Boolean,
    ): Item? {
        when (action) {
            Const.alarmOne -> {
                return createItem(item, action, "", date)
            }

            Const.alarmDay -> {
                if (premium) return createItem(
                    item,
                    action,
                    "и через день",
                    date
                )
                else toast(context)
            }

            Const.alarmWeek -> {
                if (premium) return createItem(
                    item,
                    action,
                    "и через неделю",
                    date
                )
                else toast(context)
            }

            Const.alarmMonth -> {

                if (premium) return createItem(
                    item,
                    action,
                    "и через месяц",
                    date
                )
                else toast(context)
            }

            Const.alarmYear -> {
                if (premium) return createItem(item, action, "и через год", date)
                else
                    toast(context)
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

    private fun toast(context: Context){
        try {
            Toast.makeText(
                context,
                R.string.repeadAlarmOff,
                Toast.LENGTH_SHORT
            ).show()
        }
        catch (_: RuntimeException){ }
    }


}