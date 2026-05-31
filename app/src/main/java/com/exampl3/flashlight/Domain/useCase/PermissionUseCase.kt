package com.exampl3.flashlight.Domain.useCase

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri


class PermissionUseCase @Inject constructor() {

    // Проверяем, китаец ли телефон (только им нужен автозапуск)
    fun isChinesePhone(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("xiaomi") ||
                manufacturer.contains("huawei") ||
                manufacturer.contains("honor")
    }

    // Проверяем, включена ли оптимизация батареи (нужно ли её отключать)
    fun isBatteryOptimizationEnabled(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    // Интент №1: Автозапуск
    fun getAutostartIntent(context: Context): Intent {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val intent = Intent()
        return try {
            if (manufacturer.contains("xiaomi")) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
            intent
        } catch (e: Exception) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }

    // Интент №2: Оптимизация батареи
    @SuppressLint("BatteryLife")
    fun getBatteryOptimizationIntent(context: Context): Intent {
        return try {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        } catch (e: Exception) {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
    }
}