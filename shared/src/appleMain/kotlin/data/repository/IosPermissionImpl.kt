package data.repository

import CommonConst.NOTIFICATION
import CommonConst.SOUND
import domain.repostirory.PermissionRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

class IosPermissionImpl : PermissionRepository {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override fun isChekedPermission(permissionName: String): Boolean {
        val deferred = CompletableDeferred<Boolean>()
       return when(permissionName){
            NOTIFICATION ->{
                // 1. Запускаем асинский запрос в iOS
                notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
                    if (settings != null) {
                        val isAuthorized = settings.authorizationStatus == UNAuthorizationStatusAuthorized
                        deferred.complete(isAuthorized)
                    } else {
                        deferred.complete(false)
                    }
                }
                runBlocking { deferred.await() }
            }
           SOUND ->{true}
            else -> {true}
        }
    }

    override suspend fun requestPermission(permissionName: String): Boolean {
        return when(permissionName) {
            NOTIFICATION -> {
                val deferred = CompletableDeferred<Boolean>()
                // Запрашиваем Alert (баннеры), Sound (звуки), Badge (наклейки на иконку)
                // Используем числовое значение маски (7L означает включить все три опции)
                val options = 7uL

                notificationCenter.requestAuthorizationWithOptions(options) { isGranted, error ->
                    if (error != null) {
                        deferred.complete(false)
                    } else {
                        deferred.complete(isGranted)
                    }
                }
                deferred.await()
            }
            SOUND -> true
            else -> true
        }
    }
    }
