package domain.repostirory

import data.room.model.Item

interface AlarmRepository {
    fun createAlarm(item: Item)
    fun deleteAlarm(id: Int)
}