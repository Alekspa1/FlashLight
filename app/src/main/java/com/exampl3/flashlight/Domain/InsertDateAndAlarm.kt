package com.exampl3.flashlight.Domain

import android.app.AlarmManager
import android.content.Context
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.useCase.insertDateAndTime.InsertActionAlarmByItemUseCase
import com.exampl3.flashlight.Domain.useCase.insertDateAndTime.InsertDateUseCase
import com.exampl3.flashlight.Domain.useCase.insertDateAndTime.InsertTimeUseCase
import com.exampl3.flashlight.Domain.useCase.insertOrDeleteAlarm.ChangeAlarmUseCase
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertDateAndAlarm @Inject constructor(
    private val insertDateUseCase: InsertDateUseCase,
    private val insertTimeUseCase: InsertTimeUseCase,
    private val insertActionAlarmByItem: InsertActionAlarmByItemUseCase,
    private val changeAlarm: ChangeAlarmUseCase,
    private val db: Database,
    private val pref: SharedPreferenceImpl,
) {
    private var calendarZero = Calendar.getInstance()

    suspend fun exumDateAndAction(item: Item, date: Calendar?, context: Context) {
        val dateCalendar = date ?: insertDateUseCase.exum(item, context)
        val dateAndTimeCalendar = insertTimeUseCase.exum(item, dateCalendar, context)
        if (dateAndTimeCalendar != null) {
            val action = insertActionAlarmByItem.exum(context, pref.getPremium())
            if (action != null) {
                exumAlarm(
                    item.copy(alarmTime = dateAndTimeCalendar, interval = action),
                    context,
                    true
                )
            }
        }

    }

    suspend fun exumAlarm(item: Item, context: Context, first: Boolean) {
        calendarZero = Calendar.getInstance()
        if (first) {
            //ChangeItemBeforeAlarm(item, context, item.alarmTime)
            db.CourseDao().updateItem(item.copy(changeAlarm = true))
            changeAlarm.exum(item, item.interval)

        } else {
            if (item.changeAlarm) {
                changeAlarm.exum(item, Const.DELETE_ALARM)
                db.CourseDao().updateItem(item.copy(changeAlarm = false))
            }

            if ((item.change || !item.changeAlarm) && item.alarmTime > calendarZero.timeInMillis) {
                val newItem = item.copy(change = false, changeAlarm = !item.changeAlarm)
                changeAlarm.exum(newItem, item.interval)
                db.CourseDao().updateItem(newItem)
            }
            if (!item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                alarmRepead(item, context)
            }
        }


    }

    suspend fun alarmRepead(item: Item, context: Context) {


        when (item.interval) {

            Const.ALARM_ONE -> {
                Toast.makeText(
                    context,
                    "Вы выбрали время которое уже прошло",
                    Toast.LENGTH_SHORT
                ).show()

            }

            Const.ALARM_DAY -> {
                val newItem = item.copy(
                    changeAlarm = true,
                    alarmTime = item.alarmTime + AlarmManager.INTERVAL_DAY
                )
                changeAlarm.exum(newItem, newItem.interval)
                db.CourseDao().updateItem(newItem)


            }

            Const.ALARM_WEEK -> {
                val newItem = item.copy(
                    changeAlarm = true,
                    alarmTime = item.alarmTime + AlarmManager.INTERVAL_DAY * 7
                )
                changeAlarm.exum(newItem, newItem.interval)
                db.CourseDao().updateItem(newItem)
            }

            Const.ALARM_MONTH -> {
                val newItem =
                    item.copy(changeAlarm = true, alarmTime = item.alarmTime + Const.MONTH)
                changeAlarm.exum(newItem, newItem.interval)
                db.CourseDao().updateItem(newItem)
            }

            Const.ALARM_YEAR -> {
                val newItem = item.copy(changeAlarm = true, alarmTime = addOneYear(item.alarmTime))
                changeAlarm.exum(newItem, newItem.interval)
                db.CourseDao().updateItem(newItem)
            }
        }
    }

    private fun addOneYear(dateInMillis: Long): Long {
        calendarZero.timeInMillis = dateInMillis
        calendarZero.add(Calendar.YEAR, 1) // Добавляем один год
        return calendarZero.timeInMillis
    }
}