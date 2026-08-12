package data.perository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter // нужен для параметров URL
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import domain.model.TelegramResponse
import domain.model.TelegramUpdate
import domain.model.TelegramMessage
import domain.model.TelegramUser

class TelegramSyncServiceImpl(val ktor: HttpClient) : TelegramSyncServiceRepository {

    private val BOT_TOKEN = "8748492625:AAElYdrKsBjmgoDqZZyiTQqTkuHibLegR18"
    private val MY_CHAT_ID = 706399730L // Ваш личный ID
    private var lastUpdateId = 0L

  override  fun listenToTelegramRealtime(): Flow<String> = flow {
        while (true) {
            try {
                val url = "https://api.telegram.org/bot$BOT_TOKEN/getUpdates"
                
                // Делаем длинный запрос (Long Polling)
                val response: TelegramResponse = ktor.get(url) {
                    url {
                        if (lastUpdateId != 0L) {
                            parameters.append("offset", (lastUpdateId + 1).toString())
                        }
                        parameters.append("timeout", "30") // Ждем 30 секунд
                    }
                }.body() // 💥 Ктор сам автоматически распарсит JSON в наш класс!

                if (response.ok) {
                    for (update in response.result) {
                        // Запоминаем ID, чтобы сдвигать очередь обновлений
                        if (update.updateId > lastUpdateId) {
                            lastUpdateId = update.updateId
                        }

                        val message = update.message ?: continue
                        val fromUser = message.from ?: continue
                        val text = message.text ?: continue

                        // Проверяем, что пишет именно владелец
                        if (fromUser.id == MY_CHAT_ID && text.isNotBlank()) {
                            emit(text) // Отправляем текст во ViewModel в реальном времени
                            sendConfirmation(text) // Отвечаем в чат
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                delay(5000) // Защита от бесконечного цикла при сбое сети
            }
        }
    }

    private suspend fun sendConfirmation(taskText: String) {
        try {
            val url = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage"
            ktor.get(url) {
                url {
                    parameters.append("chat_id", MY_CHAT_ID.toString())
                    parameters.append("text", "✅ Задача успешно добавлена на устройство:\n\"$taskText\"")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
