package com.exampl3.flashlight.Data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item

import com.exampl3.flashlight.Domain.repository.InsertDateAndTimeRepository
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.R

import kotlinx.coroutines.CompletableDeferred

import java.util.Calendar


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


    override suspend fun insertActionByItem(context: Context, premium: Boolean): Int? {
        val actionDeferred = CompletableDeferred<Int?>()
        DialogItemList.insertAlarm(context, object : DialogItemList.ActionInt {
            override fun onClick(action: Int) {
                when (action){
                    Const.ALARM_ONE -> actionDeferred.complete(action)
                    else -> {
                        if (premium) actionDeferred.complete(action)
                        else {
                            toast(context)
                            actionDeferred.complete(null)
                        }
                    }
                }
                //actionDeferred.complete(action)
            }
        })
        return actionDeferred.await()
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