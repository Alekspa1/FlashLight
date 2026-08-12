package domain.repostirory
import kotlinx.coroutines.flow.Flow


interface TelegramSyncServiceRepository {
fun  listenToTelegramRealtime(): Flow<String>
}
