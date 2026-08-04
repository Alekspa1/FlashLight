package data.alarmReceiwer

import CommonConst.CHANNEL_ID
import CommonConst.KEY_INTENT_CALL_BACKREADY
import CommonConst.KEY_INTENT_CALL_POSTPONE
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.dragon.shared.R
import data.perository.MultiplatrormAppSettings
import data.room.Item
import domain.repostirory.SaveDeleteImageRepositpry
import presentation.MainActivity


class NotificationBuilder(
    private val context: Context,
    private val image: SaveDeleteImageRepositpry,
    val settings: MultiplatrormAppSettings
) {
    val newRingtoneUri: Uri = settings.getUriAlarm().toUri()

    val oldRingtoneUri: Uri = settings.getOldUriAlarm().toUri()

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val atrubute =
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()


    fun input(item: Item){
        alarmPush().notify(item.id, notificationBuilder(item))
    }

    fun alarmPush(): NotificationManager {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) != null) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }

        if (newRingtoneUri != oldRingtoneUri) {

        if (notificationManager.getNotificationChannel(oldRingtoneUri.toString()) != null ) {
            notificationManager.deleteNotificationChannel(oldRingtoneUri.toString())
        }
            notificationManager.createNotificationChannel(createChanel(atrubute))
        }

        if (notificationManager.getNotificationChannel(newRingtoneUri.toString()) == null ) {
            notificationManager.createNotificationChannel(createChanel(atrubute))
        }

        settings.saveOldUriAlarm(newRingtoneUri.toString())

        return notificationManager
    }

    private fun createChanel(atrubute: AudioAttributes): NotificationChannel {
        val pattern = longArrayOf(0, 1000, 500, 1000, 500)
    return  NotificationChannel(
        newRingtoneUri.toString(),
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {

        setSound(newRingtoneUri, atrubute)
        enableVibration(true)
        vibrationPattern = pattern
        setBypassDnd(true)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    }
    }

    private fun notificationBuilder(item: Item): Notification {

        val intentCancel = Intent(context, AlarmReceiwer::class.java)
        intentCancel.setAction(KEY_INTENT_CALL_BACKREADY)
        intentCancel.putExtra(KEY_INTENT_CALL_BACKREADY, item.id)

        val canselIntent =
            PendingIntent.getBroadcast(
                context, item.id, intentCancel,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val intentPostpone = Intent(context, AlarmReceiwer::class.java)
        intentPostpone.setAction(KEY_INTENT_CALL_POSTPONE)
        intentPostpone.putExtra(KEY_INTENT_CALL_POSTPONE, item.id)

        val postponeIntent =
            PendingIntent.getBroadcast(
                context, item.id, intentPostpone,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


        val intentPush = Intent(context, MainActivity::class.java)

        val contentIntent =
            PendingIntent.getActivity(
                context, item.id, intentPush,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val fullPath = image.getUri(item.uri).removePrefix("file://")

            val bitmap: Bitmap? = try {
                if (fullPath.isNotEmpty()) {
                BitmapFactory.decodeFile(fullPath)
                } else {
                null
                }
                } catch (_: Exception) {
                null
                }
            
        // val bitmap:Bitmap? = try {
        //     MediaStore.Images.Media.getBitmap(context.contentResolver, image.getUri(item.uri).toUri())
        // } catch (_: Exception){
        //     null
        // }
        val bigIcon = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)


        val vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500)
        val builder = NotificationCompat.Builder(context, newRingtoneUri.toString())
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(item.name)
            .setContentText(item.desc)
            .setVibrate(vibrationPattern)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(bigIcon)
            .setContentIntent(contentIntent)
            .addAction(0, "Готово", canselIntent)
            .addAction(0, "Отложить", postponeIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)


        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        return notification
    }

}
