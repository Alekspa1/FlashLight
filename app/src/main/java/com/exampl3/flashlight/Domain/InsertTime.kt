package com.exampl3.flashlight.Domain

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Presentation.DialogItemList
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertTime @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val insertAlarm: InsertAlarm,
) {
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar

    fun datePickerDialog(context: Context, item: Item) {
        calendar = Calendar.getInstance()
        calendarZero = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                timePickerDialog(context, item)
            },
            calendarZero.get(Calendar.YEAR),
            calendarZero.get(Calendar.MONTH),
            calendarZero.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    } // Установрка даты

    private fun timePickerDialog(context: Context, item: Item) {
        timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                DialogItemList.insertAlarm(
                    context,
                    object : DialogItemList.ActionInt {
                        override fun onClick(action: Int) {
                            if (calendar.timeInMillis >= calendarZero.timeInMillis) {
                                proverkaFreeAndInsertStringIterval(
                                    context,
                                    item,
                                    action,
                                    calendar.timeInMillis
                                )
                            } else Toast.makeText(
                                context,
                                "Вы выбрали время которое уже прошло",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

            },
            calendarZero.get(Calendar.HOUR_OF_DAY),
            calendarZero.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    } // Установка времени

    private fun proverkaFreeAndInsertStringIterval(
        context: Context,
        item: Item,
        action: Int,
        timeCal: Long
    ) {
        when (action) {
            Const.alarmOne -> {
                insertAlarm.insertAlarm(item, action, "", timeCal)
            }

            Const.alarmDay -> {
                if (pref.getPremium()) insertAlarm.insertAlarm(
                    item,
                    action,
                    "и через день",
                    timeCal
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmWeek -> {
                if (pref.getPremium()) insertAlarm.insertAlarm(
                    item,
                    action,
                    "и через неделю",
                    timeCal
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmMonth -> {

                if (pref.getPremium()) insertAlarm.insertAlarm(
                    item,
                    action,
                    "и через месяц",
                    timeCal
                )
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }

            Const.alarmYear -> {
                if (pref.getPremium()) insertAlarm.insertAlarm(item, action, "и через год", timeCal)
                else Toast.makeText(
                    context,
                    "Повторяющиеся напоминания, доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } // проверка премиум подписки и установка текста повторения

}