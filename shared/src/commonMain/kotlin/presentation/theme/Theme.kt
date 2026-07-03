package presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.ic_del_notebook_neon
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.DrawableResource


sealed interface Theme{
    val textColor: Color
    val noteBookBackground: Color
    val noteBookBorder: Color
    val iconMicro: DrawableResource
    val iconDel: DrawableResource
    val tintAlarmOn: Color
    val cardItemBorderAlarm: Color
    val cardItemBorderTrue: Color
    val cardItemBorderFalse: Color
    val cardItemAlarm: Color
    val cardItemTrue: Color
    val cardItemFalse: Color
    val tintAlarmOff: Color
    val textDecs: Color
    val textAlarm: Color
    val chekBoxOff: ImageVector
    val chekBoxOn: ImageVector
    val chekBoxTint: Color
    val iconImage: ImageVector
    val iconTint: Color
    val iconAdd: ImageVector
    val iconAddTint: Color
    val iconDelItem: ImageVector
    val iconDelTint: Color
}

data class ThemeNeon (
    override val textColor: Color = Color.White,

    //Блокнот
    override  val noteBookBackground: Color = Color(0x9900BCD4),
    override  val noteBookBorder: Color = Color(0x9900E2FF),
    override  val iconMicro: DrawableResource = Res.drawable.ic_micro_neon,
    override  val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon,

    //Список дел
    override val tintAlarmOn: Color = Color.Yellow,
    override val tintAlarmOff: Color = Color.White,
    override val textDecs: Color = Color(0xFFB6B6B6),

    override val cardItemBorderAlarm: Color = Color(0xB3D6C000),
    override val cardItemBorderTrue: Color = Color(0xB325C800),
    override val cardItemBorderFalse: Color = Color(0xB3FB4141),

    override val cardItemAlarm: Color = Color(0x80006F7E),
    override val cardItemTrue: Color = Color(0x4D23BD00),
    override val cardItemFalse: Color = Color(0x4DF30404),

    override val textAlarm: Color = Color.Yellow,
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color.White,

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF65D4FF),
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
) : Theme

data class ThemeZabor (
    override val textColor: Color = Color.White,

    //Блокнот
    override  val noteBookBackground: Color = Color(0x9900BCD4),
    override  val noteBookBorder: Color = Color(0x9900E2FF),
    override  val iconMicro: DrawableResource = Res.drawable.ic_micro_neon,
    override  val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon,

    //Список дел
    override  val tintAlarmOn: Color = Color.Yellow,
    override val tintAlarmOff: Color = Color.White,
    override val textDecs: Color = Color(0xFFB6B6B6),
    override val cardItemBorderAlarm: Color = Color(0xFFB6B6B6),
    override val cardItemBorderTrue: Color = Color(0xFFB6B6B6),
    override val cardItemBorderFalse: Color = Color(0xFFB6B6B6),
    override val cardItemAlarm: Color = Color(0xB3D6C000),
    override val cardItemTrue: Color = Color(0xB325C800),
    override val cardItemFalse: Color = Color(0xB3FB4141),
    override  val textAlarm: Color = Color.Yellow,
    override  val chekBoxOff: ImageVector = Icons.Default.CheckBox,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val iconImage: ImageVector = Icons.Default.Image,
    override val chekBoxTint: Color = Color.White,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override  val iconAddTint: Color = Color(0xFF65D4FF),
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,

    ) : Theme
