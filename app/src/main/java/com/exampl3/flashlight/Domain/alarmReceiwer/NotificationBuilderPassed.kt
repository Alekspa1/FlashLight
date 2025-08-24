package com.exampl3.flashlight.Domain.alarmReceiwer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.LogText
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.R
import javax.inject.Inject


class NotificationBuilderPassed @Inject constructor(private val context: Application) {

    val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val atrubute =
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun input(item: Item){
        alarmPushPassed().notify(item.id!!, notificationBuilderPassed(item).build())
    }

    fun alarmPushPassed(): NotificationManager {
        if (notificationManager.getNotificationChannel(Const.CHANNEL_ID_PASSED) == null){
            notificationManager.createNotificationChannel(createChanel())
        }

        return notificationManager

    }

    fun createChanel(): NotificationChannel {
        return NotificationChannel(
            Const.CHANNEL_ID_PASSED,
            "Пропущеный", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(ringtone, atrubute)
            enableVibration(true)
        }
    }

    fun notificationBuilderPassed(item: Item): NotificationCompat.Builder {
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
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(bigTextStyle)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
        }
    }

}