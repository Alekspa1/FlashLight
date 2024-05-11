package com.exampl3.flashlight.Domain.model.alarmReceiwer

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
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.R
import javax.inject.Inject


class NotificationBuilderPassed @Inject constructor(private val context: Application) {


    fun input(item: Item){
        alarmPushPassed().notify(item.id!!, notificationBuilderPassed(item).build())
    }
    fun alarmPushPassed(): NotificationManager {
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val atrubute =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
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
                .setChannelId(Const.CHANNEL_ID_PASSED)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(bigTextStyle)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
        }
    }

}