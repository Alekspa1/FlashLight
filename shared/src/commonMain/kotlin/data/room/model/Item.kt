package data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity
@Serializable
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name")val name: String = "(Без названия)",
    @ColumnInfo(name = "change")val change: Boolean = false,
    @ColumnInfo(name = "alarmText", defaultValue = "")val uri: String = "",
    @ColumnInfo(name = "alarmTime", defaultValue = "0")val alarmTime: Long = 0,
    @ColumnInfo(name = "changeAlarm", defaultValue = "false")val changeAlarm: Boolean = false,
    @ColumnInfo(name = "interval", defaultValue = "0")val interval: Int = 0,
    @ColumnInfo(name = "category", defaultValue = "Повседневные")val category: String = "Повседневные",
    @ColumnInfo(name = "desc", defaultValue = "")val desc: String = "",
    @ColumnInfo(name = "sort", defaultValue = "0")var sort: Int = 0,
    )

