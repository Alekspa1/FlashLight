package presentation.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

sealed interface Size {
    val textMenu : TextUnit
    val textItem : TextUnit
    val lineHeightItem : TextUnit
    val drawerBottomMenuText : TextUnit

}


data class SizeNormal(
    override val textMenu: TextUnit = 23.sp,
    override val textItem: TextUnit = 18.sp,
    override val lineHeightItem: TextUnit = 20.sp,
    override val drawerBottomMenuText: TextUnit = 14.sp

) : Size