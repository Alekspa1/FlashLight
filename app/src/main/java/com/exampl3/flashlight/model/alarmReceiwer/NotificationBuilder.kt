package com.exampl3.flashlight.model.alarmReceiwer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.R
import javax.inject.Inject


class NotificationBuilder @Inject constructor(
    private val context: Application) {
    fun input(item: Item){
        alarmPush().notify(item.id!!, notificationBuilder(item).build())
    }
     fun alarmPush(): NotificationManager {
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
    private fun notificationBuilder(item: Item): NotificationCompat.Builder {

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
}