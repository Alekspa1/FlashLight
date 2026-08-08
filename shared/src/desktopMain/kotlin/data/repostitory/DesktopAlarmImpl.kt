package com.dragon.shared.data.repostitory

import CommonConst.ALARM_ONE
import data.room.CourseDao
import data.room.model.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DesktopAlarmImpl(
    private val db: CourseDao
) : AlarmRepository {

    private val scheduler = Executors.newScheduledThreadPool(2)
    private val activeAlarms = ConcurrentHashMap<Int, ScheduledFuture<*>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun createAlarm(item: Item) {
        // ОСТАВЛЯЕМ СТРОКУ! Она жизненно необходима для очистки памяти Java
        deleteAlarm(item.id)

        val currentMillis = System.currentTimeMillis()
        val delay = item.alarmTime - currentMillis

        if (delay <= 0) return

        val scheduledTask = scheduler.schedule({
            triggerAlarm(item)
        }, delay, TimeUnit.MILLISECONDS)

        activeAlarms[item.id] = scheduledTask
    }

    override fun deleteAlarm(id: Int) {
        activeAlarms.remove(id)?.cancel(true)
    }

    private fun triggerAlarm(item: Item) {
        activeAlarms.remove(item.id)
        println("⏰ Будильник '${item.name}' СРАБОТАЛ на Десктопе!")

        // Показываем русское уведомление без кракозябр (кодировка windows-1251)
        showDesktopNotification(
            title = item.name,
            message = if (item.desc.isNotEmpty()) item.desc else "Будильник сработал!"
        )

        scope.launch {
            println(item.interval)
            when (item.interval) {
                ALARM_ONE -> {
                    val updatedItem = item.copy(change = false, changeAlarm = false)
                    db.updateItem(updatedItem)
                }
                else -> {
                    // РАЗРЫВАЕМ КРУГ: Достаем репозиторий из Koin на лету строго в момент звонка
                    val alarmRepeat = GlobalContext.get().get<AlarmRepeadRepository>()

                    alarmRepeat.alarmRepead(item.id) { message ->
                        println("Десктопный лог повтора: $message")
                    }
                }
            }
        }
    }

    private fun showDesktopNotification(title: String, message: String) {
        val os = System.getProperty("os.name").lowercase()

        try {
            if (os.contains("win")) {
                val tempFile = File.createTempFile("flashlight_push", ".vbs")
                tempFile.deleteOnExit()

                // ИСПРАВЛЕНО: Явно пишем текст в кодировке Windows-1251, чтобы не было кракозябр
                val win1251 = Charset.forName("windows-1251")
                val scriptContent = "Set objShell = CreateObject(\"WScript.Shell\")\n" +
                        "objShell.Popup \"$message\", 0, \"$title\", 64"

                tempFile.writeText(scriptContent, win1251)

                ProcessBuilder("wscript", tempFile.absolutePath).start()

            } else if (os.contains("mac")) {
                val script = "display notification \"$message\" with title \"$title\""
                ProcessBuilder("osascript", "-e", script).start()
            } else {
                ProcessBuilder("notify-send", title, message).start()
            }
        } catch (e: Exception) {
            println("❌ Ошибка при отправке уведомления на ПК: ${e.message}")
        }
    }
}