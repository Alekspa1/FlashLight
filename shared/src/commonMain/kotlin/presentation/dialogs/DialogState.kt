package presentation.dialogs

data class DialogState(
    val isActive: Boolean = false,
    val isWho: String = CommonConst.DEFAULT_DIALOG)
