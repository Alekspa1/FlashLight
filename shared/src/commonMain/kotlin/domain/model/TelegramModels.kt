package domain.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


  
@Serializable
data class TelegramResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: List<TelegramUpdate>
)

@Serializable
data class TelegramUpdate(
    @SerialName("update_id") val updateId: Long,
    @SerialName("message") val message: TelegramMessage? = null
)

@Serializable
data class TelegramMessage(
    @SerialName("message_id") val messageId: Long,
    @SerialName("from") val from: TelegramUser? = null,
    @SerialName("text") val text: String? = null
)

@Serializable
data class TelegramUser(
    @SerialName("id") val id: Long,
    @SerialName("first_name") val firstName: String
)
  
