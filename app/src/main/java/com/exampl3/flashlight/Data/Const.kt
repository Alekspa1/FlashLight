package com.exampl3.flashlight.Data

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Const {
    const val BANER = "R-M-4702196-1"
    const val MEZSTR = "R-M-4702196-2"
    const val keyNoteBook = "key"
    const val keyNoteBookSize = "size"
    const val delete = 0
    const val change = 1
    const val changeItem = 2
    const val alarm = 3
    const val CHANNEL_ID = "channelID"
    const val CHANNEL_ID_PASSED = "CHANNEL_ID_PASSED"
    const val keyIntent = "keyId"
    const val keyIntentAlarm = "keyIdAlarm"
    const val keyIntentCallBackReady = "keyIntentCallBackready"
    const val keyIntentCallBackPostpone = "keyIntentCallpostpone"
    const val reboot = "android.intent.action.BOOT_COMPLETED"
    fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}