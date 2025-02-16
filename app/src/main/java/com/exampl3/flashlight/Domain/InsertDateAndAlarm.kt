package com.exampl3.flashlight.Domain

import android.app.AlarmManager
import android.content.Context
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertActionAlarmByItemUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertDateUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertStringAlarmByItemUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertTimeUseCase
import com.exampl3.flashlight.Domain.insertOrDeleteAlarm.ChangeAlarmUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertDateAndAlarm @Inject constructor(
    private val insertDateUseCase: InsertDateUseCase,
    private val insertTimeUseCase: InsertTimeUseCase,
    private val insertActionAlarmByItem: InsertActionAlarmByItemUseCase,
    private val insertStringAlarmByItem: InsertStringAlarmByItemUseCase,
    private val changeAlarm: ChangeAlarmUseCase,
    private val db: Database,
    private val pref: SharedPreferenceImpl,
) {
    private var calendarZero = Calendar.getInstance()

    suspend fun exumDate(item: Item, date: Calendar?, context: Context) {
        val dateCalendar = date ?: insertDateUseCase.exum(item, context)
        val dateAndTimeCalendar = insertTimeUseCase.exum(item, dateCalendar, context)
        if (dateAndTimeCalendar != null) {
            val action = insertActionAlarmByItem.exum(context)
            exumString(item.copy(alarmTime = dateAndTimeCalendar, interval = action), context, true)
        }

    }

    suspend fun exumString(item: Item, context: Context, first: Boolean) {
        calendarZero = Calendar.getInstance()
        if (first) {
            ChangeItemBeforeAlarm(item, context, item.alarmTime)
//            val itemCreate =
//            insertStringAlarmByItem.exum(item, context, item.interval, item.alarmTime, pref.getPremium())
//            if (itemCreate != null) {
//                db.CourseDao().updateItem(itemCreate)
//                changeAlarm.exum(itemCreate, itemCreate.interval)
//            }
        }
        else {

            //db.CourseDao().updateItem(item.copy(changeAlarm = !item.changeAlarm))

            if (item.changeAlarm) {
                changeAlarm.exum(item, Const.deleteAlarm)
                db.CourseDao().updateItem(item.copy(changeAlarm = !item.changeAlarm))
            }

            if ((item.change || !item.changeAlarm) && item.alarmTime > calendarZero.timeInMillis) {

                ChangeItemBeforeAlarm(
                    item.copy(change = false, changeAlarm = !item.changeAlarm),
                    context,
                    item.alarmTime
                )
//                db.CourseDao()
//                    .updateItem(item.copy(change = false, changeAlarm = !item.changeAlarm))

            }

            if (!item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                alarmRepead(item, context)

//                when (item.interval) {
//                    Const.alarmOne -> {
//                        Toast.makeText(
//                            context,
//                            "Вы выбрали время которое уже прошло",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        db.CourseDao().updateItem(item.copy(changeAlarm = false))
//                    }
//
//                    Const.alarmDay -> {
//                        newItem(item, context, item.alarmTime + AlarmManager.INTERVAL_DAY)
//                    }
//
//                    Const.alarmWeek -> {
//                        newItem(item, context, item.alarmTime + AlarmManager.INTERVAL_DAY * 7)
//                    }
//
//                    Const.alarmMonth -> {
//                        newItem(item, context, item.alarmTime + Const.MONTH)
//                    }
//
//                    Const.alarmYear -> {
//                        newItem(item, context, addOneYear(item.alarmTime))
//                    }
//                }
            }
        }


    }


    fun ChangeItemBeforeAlarm(item: Item, context: Context, alarmTime: Long) {

        val itemCreate =
            insertStringAlarmByItem.exum(item, context, item.interval, alarmTime, pref.getPremium())
        if (itemCreate != null) {
             CoroutineScope(Dispatchers.IO).launch {   db.CourseDao().updateItem(itemCreate)}
            changeAlarm.exum(itemCreate, itemCreate.interval)
        }
    }

    suspend fun alarmRepead(item: Item, context: Context){

        when (item.interval) {
            Const.alarmOne -> {
                Toast.makeText(
                    context,
                    "Вы выбрали время которое уже прошло",
                    Toast.LENGTH_SHORT
                ).show()

                db.CourseDao().updateItem(item.copy(changeAlarm = false))
            }

            Const.alarmDay -> {
                ChangeItemBeforeAlarm(item, context, item.alarmTime + AlarmManager.INTERVAL_DAY)
            }

            Const.alarmWeek -> {
                ChangeItemBeforeAlarm(item, context, item.alarmTime + AlarmManager.INTERVAL_DAY * 7)
            }

            Const.alarmMonth -> {
                ChangeItemBeforeAlarm(item, context, item.alarmTime + Const.MONTH)
            }

            Const.alarmYear -> {
                ChangeItemBeforeAlarm(item, context, addOneYear(item.alarmTime))
            }
        }
    }

    private fun addOneYear(dateInMillis: Long): Long {
        calendarZero.timeInMillis = dateInMillis
        calendarZero.add(Calendar.YEAR, 1) // Добавляем один год
        return calendarZero.timeInMillis
    }
}