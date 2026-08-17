package domain.model

import data.room.model.Item
import data.room.model.ListCategory
import data.room.model.SubItem
import kotlinx.serialization.Serializable

@Serializable
data class AppJsonBackup(
    val notebookText: String,
    val listCategorys: List<ListCategory>,
    val listItems: List<Item>,
    val listSubItems: List<SubItem>
)
