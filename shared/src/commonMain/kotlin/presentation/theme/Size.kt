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



data class SizeSmall(
    override val textMenu: TextUnit = 20.sp,
    override val textItem: TextUnit = 15.sp,
    override val lineHeightItem: TextUnit = 17.sp, // Уменьшили на 3
    override val drawerBottomMenuText: TextUnit = 11.sp,
    override val textNoteBook: TextUnit = 14.sp,
    override val textDesc: TextUnit = 9.sp,
    override val textAlarm: TextUnit = 9.sp,
    override val lineHeightDescAndAlarm: TextUnit = 11.sp // Уменьшили на 3
) : Size

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


data class SizeLarge(
    override val textMenu: TextUnit = 26.sp,
    override val textItem: TextUnit = 21.sp,
    override val lineHeightItem: TextUnit = 23.sp, // Увеличили на 3
    override val drawerBottomMenuText: TextUnit = 17.sp,
    override val textNoteBook: TextUnit = 20.sp,
    override val textDesc: TextUnit = 15.sp,
    override val textAlarm: TextUnit = 15.sp,
    override val lineHeightDescAndAlarm: TextUnit = 17.sp // Увеличили на 3
) : Size
