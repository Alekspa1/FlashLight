package com.exampl3.flashlight.model.alarmReceiwer

import android.app.AlarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.exampl3.flashlight.model.AlarmManagerImp
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiwer: BroadcastReceiver() {
    @Inject
    lateinit var db: GfgDatabase
    @Inject
    lateinit var alarmManager: AlarmManagerImp
    @Inject
    lateinit var notificationBuilder: NotificationBuilder
    @Inject
    lateinit var notificationBuilderPassed: NotificationBuilderPassed

    private lateinit var calendarZero: Calendar

    override fun onReceive(context: Context, intent: Intent) {
        calendarZero = Calendar.getInstance()


        when (intent.action) {
            Const.keyIntentAlarm -> {
                val item = intent.getSerializableExtra(Const.keyIntent) as Item
                //alarmPush(context).notify(item.id!!, notificationBuilder(context, item).build())
                //providesAlarmPush.notify(item.id!!, notificationBuilder(context, item).build())
                //providesAlarmPush.notify(item.id!!, notificationBuilder.notificationBuilder(item).build())
                //notificationBuilder.alarmPush().notify(item.id!!, notificationBuilder.notificationBuilder(item).build())
                notificationBuilder.input(item)
                repeatAlarm(item, "")
//                when(item.interval){
//                    Const.alarmOne->{
//                        Thread {
//                            db.CourseDao().update(item.copy(changeAlarm = false))
//                        }.start()
//                    }
//                     else ->
////                    Const.alarmDay-> {
////                        insertAlarm(item, AlarmManager.INTERVAL_DAY,"и через день")
////                    }
////
////                    Const.alarmWeek-> {
////                        insertAlarm(item,AlarmManager.INTERVAL_DAY*7,"и через неделю")
////                    }
////
////                    Const.alarmMonth-> {
////                        insertAlarm(item,Const.MONTH,"и через месяц")
////                    }
//                }

            } // Приход будильника

            Const.keyIntentCallBackReady -> {
                val item = intent.getSerializableExtra(Const.keyIntentCallBackReady) as Item
                when(item.interval){
                    Const.alarmOne->{
                        CoroutineScope(Dispatchers.IO).launch {
                            db.CourseDao()
                                .update(item.copy(change = true, changeAlarm = false))
                        }
                    }
                }
               // alarmPush(context).cancel(item.id!!)
               notificationBuilder.alarmPush().cancel(item.id!!)

            } // Когда нажал кнопку готово

            Const.keyIntentCallBackPostpone -> {
                val time = calendarZero.timeInMillis + 600000
                val item = intent.getSerializableExtra(Const.keyIntentCallBackPostpone) as Item
//                val dateFormat = "dd.MM"
//                val timeFormat = "HH:mm"
//                val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
//                val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
//                val resultDate = dateFormate.format(time)
//                val resutTime = timeFormate.format(time)
//                val result = "Напомнит: $resultDate в $resutTime"

                when(item.interval){
                    Const.alarmOne->{
//                        val newItem = item.copy(alarmTime = time, alarmText = result)
//                        Thread {
//                            db.CourseDao().update(newItem)
//                        }.start()
//                        alarmManager.alarmInsert(newItem, Const.alarmOne)
                        insertAlarm(item, time,"")
                    } else->{
                        val newItemFals = item.copy(id = item.id?.plus(1000), interval = Const.alarmRepeat)
                    alarmManager.alarmInsert(newItemFals, Const.alarmOne)
                    }
                }
                Toast.makeText(context, "Отложено на 10 минут", Toast.LENGTH_SHORT).show()
               // alarmPush(context).cancel(item.id!!)
                notificationBuilder.alarmPush().cancel(item.id!!)
            } // Когда нажал кнопку отложить

            Const.reboot -> {
                Thread {
                    db.CourseDao().getAllList().forEach { item ->
                        if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                            alarmManager.alarmInsert(item, item.interval)
                        }
                        if (item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                            notificationBuilderPassed.input(item)
//                            providesAlarmPushPassed.notify(
//                                item.id!!,
//                                notificationBuilderPassed.notificationBuilderPassed(item).build()
//                            )
                            repeatAlarm(item,"(Пропущено)")
//                            when(item.interval){
//                                Const.alarmOne-> {
//                                    db.CourseDao().update(
//                                        item.copy(
//                                            changeAlarm = false,
//                                            name = "${item.name} (Пропущено)"
//                                        )
//                                    )
//                                }
//                                else ->
////                                Const.alarmDay-> {
////                                    insertAlarm(item, AlarmManager.INTERVAL_DAY,"и через день")
////                                }
////                                Const.alarmWeek-> {
////                                    insertAlarm(item,AlarmManager.INTERVAL_DAY*7,"и через неделю")
////                                }
////                                Const.alarmMonth-> {
////                                    insertAlarm(item,Const.MONTH,"и через месяц")
////                                }
//                            }

                        }
                    }
                }.start()
            } // После перезагрузки
        }
    }
    private fun insertAlarm(item: Item,time: Long, intervalString:String){
        //val time = item.alarmTime + intervalTime
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
        val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = dateFormate.format(time)
        val resutTime = timeFormate.format(time)
        val result = "Напомнит: $resultDate в $resutTime $intervalString".trim()
        val newItem = item.copy(alarmTime = time, alarmText = result)
        CoroutineScope(Dispatchers.IO).launch {db.CourseDao().update(newItem)  }
        alarmManager.alarmInsert(newItem, item.interval)

    }

    private fun repeatAlarm(item: Item, name: String){
        when(item.interval){
            Const.alarmOne-> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().update(
                        item.copy(
                            changeAlarm = false,
                            name = "${item.name} $name".trim()
                        )
                    )
                }

            }
            Const.alarmDay-> {
                insertAlarm(item, item.alarmTime+AlarmManager.INTERVAL_DAY,"и через день")
            }
            Const.alarmWeek-> {
                insertAlarm(item,item.alarmTime+AlarmManager.INTERVAL_DAY*7,"и через неделю")
            }
            Const.alarmMonth-> {
                insertAlarm(item,item.alarmTime+Const.MONTH,"и через месяц")
            }
        }
    }




//        ringtone = RingtoneManager.getRingtone(
//            context,
//            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        )
    //calendarZero = Calendar.getInstance()
    //modelFlashLight = ViewModelFlashLight(pref)
    //Log.d("MyLog", "pacetAlarmReceiver: $modelFlashLight")

    // alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //initDb(context)
//    private fun initDb(context: Context) {
//        db = Room.databaseBuilder(
//            context,
//            GfgDatabase::class.java, "db"
//        ).build()
//    }

//    private fun alarmPush(context: Context): NotificationManager {
//        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        val atrubute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val mChannel = NotificationChannel(
//                Const.CHANNEL_ID,
//                context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
//            )
//            mChannel.setSound(ringtone, atrubute)
//            notificationManager.createNotificationChannel(mChannel)
//        }
//        return notificationManager
//
//    }

//    private fun alarmPushPassed(context: Context): NotificationManager {
//        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val atrubute =
//            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val mChannel = NotificationChannel(
//                Const.CHANNEL_ID_PASSED,
//                "Пропущеный", NotificationManager.IMPORTANCE_HIGH
//            )
//            mChannel.setSound(ringtone, atrubute)
//            notificationManager.createNotificationChannel(mChannel)
//        }
//        return notificationManager
//
//    }

//    private fun notificationBuilder(context: Context, item: Item): NotificationCompat.Builder {
//
//        val intentCancel = Intent(context, AlarmReceiwer::class.java)
//        intentCancel.setAction(Const.keyIntentCallBackReady)
//        intentCancel.putExtra(Const.keyIntentCallBackReady, item)
//
//        val canselIntent =
//            PendingIntent.getBroadcast(
//                context, item.id!!, intentCancel,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//        val intentPostpone = Intent(context, AlarmReceiwer::class.java)
//        intentPostpone.setAction(Const.keyIntentCallBackPostpone)
//        intentPostpone.putExtra(Const.keyIntentCallBackPostpone, item)
//
//        val postponeIntent =
//            PendingIntent.getBroadcast(
//                context, item.id!!, intentPostpone,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//
//        val intentPush = Intent(context, MainActivity::class.java)
//
//        val contentIntent =
//            PendingIntent.getActivity(
//                context, item.id!!, intentPush,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//        val bigTextStyle = NotificationCompat.BigTextStyle()
//
//        return context.let {
//            NotificationCompat.Builder(it, Const.CHANNEL_ID)
//                .setSmallIcon(R.drawable.icon)
//                .setContentTitle(item.name)
//                .setChannelId(Const.CHANNEL_ID)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                .setStyle(bigTextStyle)
//                .setContentIntent(contentIntent)
//                .addAction(0, "Готово", canselIntent)
//                .addAction(0, "Отложить", postponeIntent)
//                .setAutoCancel(true)
//        }
//    }

//    private fun notificationBuilderPassed(
//        context: Context,
//        item: Item
//    ): NotificationCompat.Builder {
//
//
//        val intentPush = Intent(context, MainActivity::class.java)
//
//        val contentIntent =
//            PendingIntent.getActivity(
//                context, item.id!!, intentPush,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//        val bigTextStyle = NotificationCompat.BigTextStyle()
//
//        return context.let {
//            NotificationCompat.Builder(it, Const.CHANNEL_ID_PASSED)
//                .setSmallIcon(R.drawable.icon)
//                .setContentTitle("Вы пропустили уведомление")
//                .setContentText(item.name)
//                .setChannelId(Const.CHANNEL_ID_PASSED)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                .setStyle(bigTextStyle)
//                .setContentIntent(contentIntent)
//                .setAutoCancel(true)
//        }
//    }

}
