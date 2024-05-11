package com.exampl3.flashlight.model

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.Presentation.FragmentList
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.Presentation.ViewModelFlashLight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
@AndroidEntryPoint
class InsertTime @Inject constructor(
    private val alarmInsert: AlarmManagerImp,
    ) : MainActivity() {



    fun deleteAlertDialog(context: Context, item: Item) {
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete {
            override fun onClick(flag: Boolean) {
                if (flag) {
                    CoroutineScope(
                        Dispatchers
                            .IO
                    ).launch {
                        changeAlarmItem(item, Const.deleteAlarm)
                        db.CourseDao().delete(item)


                    }

                }
            }
        })
    } // Подтверждение на удаление

    fun changeAlarmItem(item: Item, action: Int) {
        alarmInsert.alarmInsert(
            item,
            action
        )

    } // Изменение заметки
     fun insertAlarm(item: Item, result: Int, intervalText: String, timeCal: Long) {
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val date = SimpleDateFormat(dateFormat, Locale.US)
        val time = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = date.format(timeCal)
        val resutTime = time.format(timeCal)
        val newAlarmText = "Напомнит: $resultDate в $resutTime"
        val newitem = item.copy(
            changeAlarm = true,
            alarmText = "$newAlarmText $intervalText",
            alarmTime = timeCal,
            change = false,
            name = item.name,
            interval = result
        )
         CoroutineScope(Dispatchers.IO).launch {db.CourseDao().update(newitem)  }
        changeAlarmItem(newitem, result)

    } // установка будильника

     fun insertAlarmRepeat(item: Item, intervalTime: Long, intervalString: String) {
        val time = item.alarmTime + intervalTime
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
        val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = dateFormate.format(time)
        val resutTime = timeFormate.format(time)
        val result = "Напомнит: $resultDate в $resutTime $intervalString"
        val newItem =
            item.copy(alarmTime = time, alarmText = result, changeAlarm = !item.changeAlarm)
        Thread {
            db.CourseDao().update(newItem)
        }.start()
        changeAlarmItem(newItem, newItem.interval)

    } // установка повторяющегося будильника

}