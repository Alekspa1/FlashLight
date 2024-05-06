package com.exampl3.flashlight.Domain

import android.app.AlarmManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.Presentation.ViewModelFlashLight
import com.exampl3.flashlight.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmReceiwer : BroadcastReceiver() {
    private lateinit var db: GfgDatabase
    private lateinit var modelFlashLight: ViewModelFlashLight
    private lateinit var ringtone: Ringtone
    private lateinit var alarmManager: AlarmManager
    private lateinit var calendarZero: Calendar

    override fun onReceive(context: Context, intent: Intent) {
        ringtone = RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
        calendarZero = Calendar.getInstance()
        modelFlashLight = ViewModelFlashLight()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        initDb(context)

        when (intent.action) {
            Const.keyIntentAlarm -> {
                val item = intent.getSerializableExtra(Const.keyIntent) as Item
                alarmPush(context).notify(item.id!!, notificationBuilder(context, item).build())
                when(item.interval){

                    Const.alarmOne->{
                        Thread {
                            db.CourseDao().update(item.copy(changeAlarm = false))
                        }.start()
                    }

                    Const.alarmDay-> {
                        insertAlarm(item,context, AlarmManager.INTERVAL_DAY,"и через день")
                    }

                    Const.alarmWeek-> {
                        insertAlarm(item,context,AlarmManager.INTERVAL_DAY*7,"и через неделю")
                    }

                    Const.alarmMonth-> {
                        insertAlarm(item,context,Const.MONTH,"и через месяц")
                    }
                }

            } // Приход будильника

            Const.keyIntentCallBackReady -> {
                val item = intent.getSerializableExtra(Const.keyIntentCallBackReady) as Item
                when(item.interval){
                    Const.alarmOne->{
                        Thread {
                            db.CourseDao()
                                .update(item.copy(change = true, changeAlarm = false))
                        }.start()
                    }
                }
                alarmPush(context).cancel(item.id!!)

            } // Когда нажал кнопку готово

            Const.keyIntentCallBackPostpone -> {
                //val time = calendarZero.timeInMillis + 600000
                val time = calendarZero.timeInMillis + 60000
                val item = intent.getSerializableExtra(Const.keyIntentCallBackPostpone) as Item
                val dateFormat = "dd.MM"
                val timeFormat = "HH:mm"
                val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
                val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
                val resultDate = dateFormate.format(time)
                val resutTime = timeFormate.format(time)
                val result = "Напомнит: $resultDate в $resutTime"
                CoroutineScope(Dispatchers.IO).launch {db.CourseDao().getAllList().forEach{
                    Log.d("MyLog", "List: $it")
                }  }

                when(item.interval){
                    Const.alarmOne->{
                        val newItem = item.copy(alarmTime = time, alarmText = result)
                        Thread {
                            db.CourseDao().update(newItem)
                        }.start()
                        modelFlashLight.alarmInsert(newItem, time, context, alarmManager, Const.alarmOne)
                    } else->{
                        val newItemFals = item.copy(id = item.id?.plus(1000), interval = Const.alarmRepeat)
                    modelFlashLight.alarmInsert(newItemFals, time, context, alarmManager, Const.alarmOne)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
                alarmPush(context).cancel(item.id!!)
            } // Когда нажал кнопку отложить

            Const.reboot -> {
                Thread {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            modelFlashLight.alarmInsert(item, item.alarmTime, context, alarmManager, item.interval)
                        }
                        if (item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                            alarmPushPassed(context).notify(
                                item.id!!,
                                notificationBuilderPassed(context, item).build()
                            )
                            when(item.interval){
                                Const.alarmOne-> {
                                    db.CourseDao().update(
                                        item.copy(
                                            changeAlarm = false,
                                            name = "${item.name} (Пропущено)"
                                        )
                                    )
                                }
                                Const.alarmDay-> {
                                    insertAlarm(item,context, AlarmManager.INTERVAL_DAY,"и через день")
                                }
                                Const.alarmWeek-> {
                                    insertAlarm(item,context,AlarmManager.INTERVAL_DAY*7,"и через неделю")
                                }
                                Const.alarmMonth-> {
                                    insertAlarm(item,context,Const.MONTH,"и через месяц")
                                }
                            }

                        }
                    }
                }.start()
            } // После перезагрузки
        }
    }

    private fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            GfgDatabase::class.java, "db"
        ).build()
    }

    private fun alarmPush(context: Context): NotificationManager {
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val atrubute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                Const.CHANNEL_ID,
                context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.setSound(ringtone, atrubute)
            notificationManager.createNotificationChannel(mChannel)
        }
        return notificationManager

    }

    private fun alarmPushPassed(context: Context): NotificationManager {
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val atrubute =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                Const.CHANNEL_ID_PASSED,
                "Пропущеный", NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.setSound(ringtone, atrubute)
            notificationManager.createNotificationChannel(mChannel)
        }
        return notificationManager

    }

    private fun notificationBuilder(context: Context, item: Item): NotificationCompat.Builder {

        val intentCancel = Intent(context, AlarmReceiwer::class.java)
        intentCancel.setAction(Const.keyIntentCallBackReady)
        intentCancel.putExtra(Const.keyIntentCallBackReady, item)

        val canselIntent =
            PendingIntent.getBroadcast(
                context, item.id!!, intentCancel,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val intentPostpone = Intent(context, AlarmReceiwer::class.java)
        intentPostpone.setAction(Const.keyIntentCallBackPostpone)
        intentPostpone.putExtra(Const.keyIntentCallBackPostpone, item)

        val postponeIntent =
            PendingIntent.getBroadcast(
                context, item.id!!, intentPostpone,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


        val intentPush = Intent(context, MainActivity::class.java)

        val contentIntent =
            PendingIntent.getActivity(
                context, item.id!!, intentPush,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val bigTextStyle = NotificationCompat.BigTextStyle()

        return context.let {
            NotificationCompat.Builder(it, Const.CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(item.name)
                .setChannelId(Const.CHANNEL_ID)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(bigTextStyle)
                .setContentIntent(contentIntent)
                .addAction(0, "Готово", canselIntent)
                .addAction(0, "Отложить", postponeIntent)
                .setAutoCancel(true)
        }
    }

    private fun notificationBuilderPassed(
        context: Context,
        item: Item
    ): NotificationCompat.Builder {


        val intentPush = Intent(context, MainActivity::class.java)

        val contentIntent =
            PendingIntent.getActivity(
                context, item.id!!, intentPush,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val bigTextStyle = NotificationCompat.BigTextStyle()

        return context.let {
            NotificationCompat.Builder(it, Const.CHANNEL_ID_PASSED)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Вы пропустили уведомление")
                .setContentText(item.name)
                .setChannelId(Const.CHANNEL_ID_PASSED)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(bigTextStyle)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
        }
    }
    private fun insertAlarm(item: Item, context: Context,intervalTime: Long, intervalString:String){
        val time = item.alarmTime + intervalTime
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
        val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = dateFormate.format(time)
        val resutTime = timeFormate.format(time)
        val result = "Напомнит: $resultDate в $resutTime $intervalString"
        val newItem = item.copy(alarmTime = time, alarmText = result)
        Thread {
            db.CourseDao().update(newItem)
        }.start()
        modelFlashLight.alarmInsert(newItem, time, context, alarmManager, item.interval)

    }
}
