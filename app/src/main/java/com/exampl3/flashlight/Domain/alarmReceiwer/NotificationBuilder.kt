package com.exampl3.flashlight.Domain.alarmReceiwer

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
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
        intentCancel.setAction(Const.KEY_INTENT_CALL_BACKREADY)
        intentCancel.putExtra(Const.KEY_INTENT_CALL_BACKREADY, item)

        val canselIntent =
            PendingIntent.getBroadcast(
                context, item.id!!, intentCancel,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val intentPostpone = Intent(context, AlarmReceiwer::class.java)
        intentPostpone.setAction(Const.KEY_INTENT_CALL_POSTPONE)
        intentPostpone.putExtra(Const.KEY_INTENT_CALL_POSTPONE, item)

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
        val bitmap:Bitmap? = try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, item.alarmText.toUri())
        } catch (_: Exception){
            null
        }
        val bigIcon = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)





        return context.let {
            NotificationCompat.Builder(it, Const.CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(item.name)
                .setContentText(item.desc)
                .setChannelId(Const.CHANNEL_ID)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setStyle(bigIcon)
                .setContentIntent(contentIntent)
                .addAction(0, "Готово", canselIntent)
                .addAction(0, "Отложить", postponeIntent)
                .setAutoCancel(true)

        }
    }

}