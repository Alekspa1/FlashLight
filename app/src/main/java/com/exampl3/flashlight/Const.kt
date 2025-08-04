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
    const val DELETE = 10
    const val CHANGE = 11
    const val CHANGE_ITEM = 12
    const val ALARM = 13
    const val IMAGE = 14
    const val CHANNEL_ID = "channelID"
    const val CHANNEL_ID_PASSED = "CHANNEL_ID_PASSED"
    const val KEY_INTENT = "keyId"
    const val KEY_INTENT_ALARM = "keyIdAlarm"
    const val KEY_INTENT_CALL_BACKREADY = "keyIntentCallBackready"
    const val KEY_INTENT_CALL_POSTPONE = "keyIntentCallpostpone"
    const val REBOOT = "android.intent.action.BOOT_COMPLETED"
    const val ALARM_ONE = 0
    const val ALARM_DAY = 1
    const val ALARM_WEEK = 2
    const val ALARM_MONTH = 3
    const val ALARM_YEAR = 4
    const val DELETE_ALARM = 5
    const val ALARM_REPEAT = 6
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

    const val SORT_SETTINGS = "SORT_SETTINGS"
    const val SORT_STANDART = "SORT_STANDART"
    const val SORT_USER = "SORT_USER"



    fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}