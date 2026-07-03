package com.dragon.shared.data.repostitory

import CommonConst.ALARM_ONE
import MainViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DesktopAlarmImpl(
    private val alarmRepeatRepositoryLazy:  Lazy<AlarmRepeadRepository>,
    private val db: CourseDao
) : AlarmRepository {

    private val alarmRepeatRepository: AlarmRepeadRepository
        get() = alarmRepeatRepositoryLazy.value
    // Пул потоков, который будет отсчитывать время в фоне
    private val scheduler = Executors.newScheduledThreadPool(2)

    // Хранилище запущенных таймеров, чтобы их можно было отменять по ID
    private val activeAlarms = ConcurrentHashMap<Int, ScheduledFuture<*>>()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun createAlarm(item: Item) {
        // 1. Если будильник с таким ID уже тикает, сначала сбрасываем его
        deleteAlarm(item.id)

        val currentMillis = System.currentTimeMillis()
        val delay = item.alarmTime - currentMillis

        // Если время будильника уже в прошлом, игнорируем запуск
        if (delay <= 0) return

        // 2. Планируем задачу на выполнение через нужный delay (в миллисекундах)
        val scheduledTask = scheduler.schedule({
            triggerAlarm(item)
        }, delay, TimeUnit.MILLISECONDS)

        // 3. Сохраняем ссылку на таймер, чтобы удалить его, если юзер передумает
        activeAlarms[item.id] = scheduledTask
    }

    override fun deleteAlarm(id: Int) {
        // Достаем таймер из мапы и жестко отменяем его (true — прервать поток)
        activeAlarms.remove(id)?.cancel(true)
    }

    private fun triggerAlarm(item: Item) {
        activeAlarms.remove(item.id)
        showDesktopNotification(
            title = item.name,
            message = if (item.desc.isNotEmpty()) item.desc else "Будильник сработал!"
        )

        scope.launch {
            // Точная копия логики processingAlarm из Android ресивера:
            when (item.interval) {
                ALARM_ONE -> {
                    // Если одноразовый — гасим тумблеры и выключаем в БД
                    val updatedItem = item.copy(
                        change = false,
                        changeAlarm = false
                    )
                    // Пишем напрямую в db, так как мы уже в фоновом IO потоке
                    db.updateItem(updatedItem)
                }

                else -> {
                    // Если повторяющийся — вызываем наш KMP репозиторий повторов
                    alarmRepeatRepository.alarmRepead(item.id) { message ->
                        println("Десктопный лог повтора: $message")
                    }
                }
            }
        }

    }

    private val trayScheduler = Executors.newSingleThreadScheduledExecutor()

    private fun showDesktopNotification(title: String, message: String) {
        if (!SystemTray.isSupported()) return

        val tray = SystemTray.getSystemTray()
        val image = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        val trayIcon = TrayIcon(image, "FlashLight Alarm").apply {
            isImageAutoSize = true
        }

        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
        } catch (e: Exception) {
            e.printStackTrace()
            // Если упало на displayMessage, гарантированно убираем иконку сразу
            try { tray.remove(trayIcon) } catch (_: Exception) {}
            return
        }

        // Планируем удаление через 5 секунд без блокировки потока (Thread.sleep)
        trayScheduler.schedule({
            try {
                tray.remove(trayIcon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 5, TimeUnit.SECONDS)
    }
}