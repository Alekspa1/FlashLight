package com.exampl3.flashlight

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.core.content.ContextCompat

object Const {
    const val RUSTORE = "https://apps.rustore.ru/app/com.exampl3.flashlight"
    const val APP_GALERY = "https://flashlightandtodolist.drru.agconnect.link/d6zW"
    var premium_KEY = "premium_KEY"
    private val calendar: Calendar = Calendar.getInstance()
    private val dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    private val dayInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
    const val BANER = "R-M-4702196-1"
    const val MEZSTR = "R-M-4702196-2"
    const val keyNoteBook = "key"
    const val delete = 10
    const val change = 11
    const val changeItem = 12
    const val alarm = 13
    const val CHANNEL_ID = "channelID"
    const val CHANNEL_ID_PASSED = "CHANNEL_ID_PASSED"
    const val keyIntent = "keyId"
    const val keyIntentAlarm = "keyIdAlarm"
    const val keyIntentCallBackReady = "keyIntentCallBackready"
    const val keyIntentCallBackPostpone = "keyIntentCallpostpone"
    const val reboot = "android.intent.action.BOOT_COMPLETED"
    const val alarmOne = 0
    const val alarmDay = 1
    const val alarmWeek = 2
    const val alarmMonth = 3
    const val alarmYear = 4
    const val deleteAlarm = 5
    const val alarmRepeat = 6
    val MONTH = AlarmManager.INTERVAL_DAY* dayInMonth
    //val YEAR = AlarmManager.INTERVAL_DAY* dayInYear




    fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}