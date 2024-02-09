package com.exampl3.flashlight.Data

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.core.content.ContextCompat

object Const {
    private val calendar: Calendar = Calendar.getInstance()
    private val dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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
    const val deleteAlarmRepeat = 4
    const val alarmRepeat = 5
    var MONTH = AlarmManager.INTERVAL_DAY*dayInMonth
    var premium = false
    fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}