package domain.repostirory


interface AlarmRepeadRepository {
    suspend fun alarmRepead(id: Int,sendMessage : (String) -> Unit = {})

}