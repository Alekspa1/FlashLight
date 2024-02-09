package com.exampl3.flashlight.Domain.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Item(
    @PrimaryKey var id: Int?,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "change")val change: Boolean = false,
    @ColumnInfo(name = "alarmText", defaultValue = "1.0")val alarmText: String = "",
    @ColumnInfo(name = "alarmTime", defaultValue = "1.0")val alarmTime: Long = 0,
    @ColumnInfo(name = "changeAlarm", defaultValue = "1.0")val changeAlarm: Boolean = false,
    @ColumnInfo(name = "changeAlarmRepeat", defaultValue = "1.0")val changeAlarmRepeat: Boolean = false,
    @ColumnInfo(name = "interval", defaultValue = "1.0")val interval: Int = 0,
    ): Serializable
