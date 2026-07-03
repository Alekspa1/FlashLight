package data.alarmReceiwer

import CommonConst.ALARM_ONE
import CommonConst.ALARM_REPEAT
import CommonConst.KEY_INTENT
import CommonConst.KEY_INTENT_ALARM
import CommonConst.KEY_INTENT_CALL_BACKREADY
import CommonConst.KEY_INTENT_CALL_POSTPONE
import CommonConst.REBOOT
import CommonConst.TEN_MINUTES
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import android.os.Build
import android.util.Log
import data.repostitory.AndroidAlarmImpl

import data.room.CourseDao
import data.room.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject



import java.util.Calendar
import kotlin.getValue


class AlarmReceiwer : BroadcastReceiver(), KoinComponent {



    private val db: CourseDao by inject()
    private val notificationBuilder: NotificationBuilder by inject()
    private val notificationBuilderPassed: NotificationBuilderPassed by inject()
    private val alarm : AlarmRepository by inject ()
    private val alarmRepeat: AlarmRepeadRepository by inject ()

    private lateinit var calendarZero: Calendar


    override fun onReceive(context: Context, intent: Intent) {

        calendarZero = Calendar.getInstance()

        val pendingResult = goAsync() // тут я говорю подожди, пока не убивай ресивер, у меня там корутина
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try{
             when (intent.action) {

            KEY_INTENT_ALARM -> {
                val item = getItemFromIntent(intent, KEY_INTENT)

                withContext(Dispatchers.Main){notificationBuilder.input(item)}

                processingAlarm(item, "")

            } // Приход будильника

            KEY_INTENT_CALL_BACKREADY -> {
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_BACKREADY)
                withContext(Dispatchers.Main){notificationBuilder.alarmPush().cancel(item.id)}
                when (item.interval) {
                    ALARM_ONE -> {
                            db.updateItem(item.copy(change = true, changeAlarm = false))
                    }
                }
                

            } // Когда нажал кнопку готово

            KEY_INTENT_CALL_POSTPONE -> {

                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_POSTPONE)
                withContext(Dispatchers.Main){
                    notificationBuilder.alarmPush().cancel(item.id)
                }
                when (item.interval) {
                    ALARM_ONE -> {
                        val newItem = item.copy(changeAlarm = true, alarmTime = time)
                            db.updateItem(newItem)

                            alarm.createAlarm(newItem)
                    }

                    else -> {
                        val newItemFals = item.copy(
                            id = item.id*-1,
                            interval = ALARM_REPEAT,
                            alarmTime = time
                        )
                        alarm.createAlarm(newItemFals)
                    }
                }

                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    Toast.makeText(
                        context.applicationContext,
                        "Отложено на 10 минут",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            
                
            } // Когда нажал кнопку отложить

            REBOOT -> {
                Log.d("MyLog", "REBOOT")
                    db.getActiveAlarms().forEach { item ->
                        if (item.alarmTime > calendarZero.timeInMillis) {
                            alarm.createAlarm(item)
                        }
                        else {
                            withContext(Dispatchers.Main){notificationBuilderPassed.input(item)}
                            processingAlarm(item, "(Пропущено)")
                        }
                    }
                Log.d("MyLog", "REBOOT END")

            } // После перезагрузки
        }    
            }
            
            catch(e: Exception){Log.d("MyLog", "$e -> REBOOT END")}
            
            finally{pendingResult.finish()}

            
        }

        
    }

    private suspend fun processingAlarm(item: Item, value: String) {
            when (item.interval) {
                ALARM_ONE -> {
                    db.updateItem(
                        item.copy(
                            change = false,
                            changeAlarm = false,
                            name = "${item.name} $value".trim()
                        )
                    )
                }

                else -> {
                        alarmRepeat.alarmRepead(item.id)

                }

            }

    } // Установка повторяющихся будильников

   private suspend fun getItemFromIntent(intent: Intent, key: String): Item {
        val id = intent.getIntExtra(key,0)
       return db.getItemFromId(id)
        }


}

