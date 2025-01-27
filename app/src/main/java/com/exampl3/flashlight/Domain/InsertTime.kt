package com.exampl3.flashlight.Domain

import android.content.Context
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.Presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
@AndroidEntryPoint
class InsertTime @Inject constructor(
    private val alarmInsert: AlarmManagerImp,
    ) : MainActivity() {


    fun deleteAlertDialog(context: Context, item: Item) {
        DialogItemList.AlertDelete(context, object : DialogItemList.ActionTrueOrFalse {
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
     fun insertAlarm(item: Item, interval: Int, intervalText: String, alareTime: Long) {
        val dateFormat = "dd.MM.yyyy"
        val timeFormat = "HH:mm"
        val date = SimpleDateFormat(dateFormat, Locale.US)
        val time = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = date.format(alareTime)
        val resutTime = time.format(alareTime)
        val newAlarmText = "Напомнит: $resultDate в $resutTime"
        val newitem = item.copy(
            changeAlarm = true,
            alarmText = "$newAlarmText $intervalText",
            alarmTime = alareTime,
            change = false,
            name = item.name,
            interval = interval
        )
         CoroutineScope(Dispatchers.IO).launch {db.CourseDao().update(newitem)  }
        changeAlarmItem(newitem, interval)

    } // установка будильника

}