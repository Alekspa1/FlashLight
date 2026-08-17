package data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity
@Serializable
data class ListCategory (
    @PrimaryKey var id: Int?,
    @ColumnInfo(name = "name")val name: String,
)