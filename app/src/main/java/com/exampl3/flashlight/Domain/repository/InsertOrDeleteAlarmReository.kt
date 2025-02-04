package com.exampl3.flashlight.Domain.repository

import com.exampl3.flashlight.Data.Room.Item

interface InsertOrDeleteAlarmReository {

    fun changeAlarm(item: Item, action: Int)
}