package com.exampl3.flashlight.Domain.alarmReceiwer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob

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

        val pendingResult = goAsync() // тут я говорю подожди, пока не убивай ресивер, у меня там корутина
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try{
             when (intent.action) {

            KEY_INTENT_ALARM -> {
                val item = getItemFromIntent(intent, Const.KEY_INTENT) ?: return@launch
                withContext(Dispatchers.Main){notificationBuilder.input(item)}
                
                repeatAlarm(item, "", context)

            } // Приход будильника

            KEY_INTENT_CALL_BACKREADY -> {
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_BACKREADY) ?: return@launch
                withContext(Dispatchers.Main){notificationBuilder.alarmPush().cancel(item.id!!)}
                when (item.interval) {
                    ALARM_ONE -> {
                            db.CourseDao()
                                .updateItem(item.copy(change = true, changeAlarm = false))
                    }
                }
                

            } // Когда нажал кнопку готово

            KEY_INTENT_CALL_POSTPONE -> {
                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_POSTPONE) ?: return@launch
                withContext(Dispatchers.Main){notificationBuilder.alarmPush().cancel(item.id!!)}
                when (item.interval) {
                    ALARM_ONE -> {
                        val newItem = item.copy(changeAlarm = true, alarmTime = time)
                            db.CourseDao().updateItem(newItem)
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
                withContext(Dispatchers.Main){Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()}
            
                
            } // Когда нажал кнопку отложить

            REBOOT -> {
                    db.CourseDao().getActiveAlarms().forEach { item ->
                        if (item.alarmTime > calendarZero.timeInMillis) {
                            changeAlarm.exum(item, item.interval)
                        }
                        else {
                            withContext(Dispatchers.Main){notificationBuilderPassed.input(item)}
                            repeatAlarm(item, "(Пропущено)", context)

                        }
                    }
                

            } // После перезагрузки
        }    
            }
            
            catch(e: Exception){}
            
            finally{pendingResult.finish()}

            
        }

        
    }

    private suspend fun repeatAlarm(item: Item, value: String, context: Context) {
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
        

    } // Установка повторяющихся будильников

   private fun getItemFromIntent(intent: Intent, key: String): Item? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getSerializableExtra(key, Item::class.java)
    } else {
        @Suppress("DEPRECATION")
        intent.getSerializableExtra(key) as? Item
    }
}


}

