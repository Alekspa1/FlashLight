package presentation.dialogs

import data.room.model.Item
import data.room.model.ListCategory

data class DialogState(
    val isWho: String = CommonConst.DEFAULT_DIALOG,
    val item: Item? = null,
    val category : ListCategory? = null,
    val calendar: Boolean = false,
    val date: Long = 0L,

)

