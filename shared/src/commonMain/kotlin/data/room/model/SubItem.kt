package data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(
    tableName = "sub_items",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["idTask"],
            onDelete = ForeignKey.CASCADE // Самое важное: удалил дело -> подзадачи стерлись сами
        )
    ],
    indices = [Index(value = ["idTask"])] // Индекс, чтобы поиск по idTask летал
)
@Serializable
data class SubItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "idTask") val idTask: Int, // Ссылка на id главного дела Item
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "change") val change: Boolean = false,
    @ColumnInfo(name = "sort") var sort: Int = 0
)