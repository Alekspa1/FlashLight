package com.exampl3.flashlight

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.core.content.ContextCompat

object Const {
    const val RUSTORE = "https://apps.rustore.ru/app/com.exampl3.flashlight"
    const val AUTHORIZED_RUSTORE = "rustore://auth/"
    const val APP_GALERY = "https://flashlightandtodolist.drru.agconnect.link/d6zW"
    const val PREMIUM_KEY = "premium_KEY"
    private val calendar: Calendar = Calendar.getInstance()
    private val dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    const val BANER = "R-M-4702196-1"
    const val TEN_MINUTES = 600000

    const val KEY_NOTE_BOOK = "key"
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
    const val ONE_MONTH = "premium_version_podpiska_flash_light"
    const val SIX_MONTH = "premium_version_podpiska_six_month_flash_light"
    const val ONE_YEAR = "premium_version_podpiska_one_year_flash_light"
    const val FOREVER = "premium_version_flash_light"
    val MONTH = AlarmManager.INTERVAL_DAY* dayInMonth
    val PURCHASE_LIST = listOf(
        FOREVER,
        ONE_MONTH,
        SIX_MONTH,
        ONE_YEAR,
        )
    const val NOT_AUTHORIZED = "RuStore User Not Authorized"
    const val DONATE = "https://www.tinkoff.ru/rm/r_yDLrspQXuU.pghPicassj/iZZa112656"



    fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}