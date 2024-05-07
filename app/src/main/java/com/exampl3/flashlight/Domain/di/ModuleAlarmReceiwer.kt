package com.exampl3.flashlight.Domain.di

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
import com.exampl3.flashlight.Domain.AlarmReceiwer
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Presentation.MainActivity
import com.exampl3.flashlight.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleAlarmReceiwer {

    @Provides
    @Singleton
    fun providesAlarmPush(context: Application): NotificationManager {
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


}