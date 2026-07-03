package domain.repostirory

import data.room.Item

interface AlarmRepository {

    fun createAlarm(item: Item)

    fun deleteAlarm(id: Int)

}