package data.room.model 

import kotlinx.serialization.Serializable
import data.room.model.Item
import data.room.model.SubItem

@Serializable
data class ItemWithSubItems(
    val item: Item,
    val subItems: List<SubItem>
)
