package presentation.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

sealed interface Size {
    val textMenu : TextUnit
    val textItem : TextUnit
    val textDesc : TextUnit
    val textAlarm : TextUnit
    val textNoteBook : TextUnit
    val lineHeightItem : TextUnit
    val lineHeightDescAndAlarm : TextUnit
    val drawerBottomMenuText : TextUnit

}


data class SizeNormal(
    override val textMenu: TextUnit = 23.sp,
    override val textItem: TextUnit = 18.sp,
    override val lineHeightItem: TextUnit = 20.sp,
    override val drawerBottomMenuText: TextUnit = 14.sp,
    override val textNoteBook: TextUnit = 17.sp,
    override val textDesc: TextUnit = 12.sp,
    override val textAlarm: TextUnit = 12.sp,
    override val lineHeightDescAndAlarm: TextUnit = 14.sp

) : Size
