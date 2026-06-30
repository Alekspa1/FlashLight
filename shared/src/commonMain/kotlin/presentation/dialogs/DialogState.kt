package presentation.dialogs

import data.room.Item

data class DialogState(
    val isActive: Boolean = false,
    val isWho: String = CommonConst.DEFAULT_DIALOG,
    val item: Item? = null
)

