package data.repository

import data.room.model.Item
import domain.repostirory.AlarmRepository

import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNNotificationRequest
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970

class IosAlarmImpl : AlarmRepository {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override fun createAlarm(item: Item) {
        // 1. Создаем контент напоминания
        val content = UNMutableNotificationContent().apply {
            setTitle("FOCUS")
            setBody(item.name) // Текст вашей тудушки (например, "Купить молоко")
            setSound(UNNotificationSound.defaultSound()) // Тот самый дефолтный звук без раздувания приложения

            // Передаем ID в userInfo, чтобы если пользователь кликнет на пуш,
            // приложение могло понять, какую именно задачу открыть
            setUserInfo(mapOf("item_id" to item.id))
        }

        // 2. Конвертируем Long (Timestamp в миллисекундах из item.alarmTime) в компоненты даты iOS
        val date = NSDate.dateWithTimeIntervalSince1970(item.alarmTime / 1000.0)
        val calendar = NSCalendar.currentCalendar()
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = date
        )

        // 3. Создаем триггер на точное время (календарный триггер)
        // repeats = false означает, что это разовый будильник
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, repeats = false)

        // 4. Создаем уникальный идентификатор для iOS на основе ID из вашей БД
        val requestId = item.id.toString()

        // 5. Регистрируем запрос в системе
        val request = UNNotificationRequest.requestWithIdentifier(requestId, content, trigger)

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                // Ошибка планирования (например, нет разрешений)
                println("FOCUS Error: Не удалось поставить напоминание: ${error.localizedDescription}")
            }
        }
    }

    override fun deleteAlarm(id: Int) {
        // На iOS удаление происходит по строковому ID запроса
        val requestId = id.toString()

        // Удаляем как уже сработавшие, так и еще только ожидающие уведомления
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(requestId))
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(requestId))
    }
}