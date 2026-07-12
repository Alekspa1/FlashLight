package presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_drawer_neon
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.ic_del_notebook_neon
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.DrawableResource


sealed interface Theme{
    val backgroundStart : DrawableResource
    val backgroundDrawer : DrawableResource
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
    val textDesc: Color
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
    val iconDrawerEveryday : ImageVector
    val iconDrawerShare : ImageVector
    val iconDrawerPremium : ImageVector
    val tintPremiumOn : Color
    val tintPremiumOff : Color
    val iconDrawerUpdateOff : ImageVector
    val iconDrawerUpdateOn : ImageVector
    val iconDrawerSettigs : ImageVector
    val backgroundDialog : Color

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
    override val textDesc: Color = Color(0xFFB6B6B6),


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
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color.Yellow,
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF424242),
    override val backgroundStart: DrawableResource = Res.drawable.background_neon,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon

    //override val iconDrawerPremiumOff: ImageVector = ,
    //override val iconDrawerSettigs: ImageVector = ,

    //Дравер

) : Theme

data class ThemeZabor (
    override val textColor: Color = Color.Black,

    // Блокнот (Нежные пастельные и деревянные тона)
    override val noteBookBackground: Color = Color(0x33D7CCC8), // Светлый полупрозрачный беж
    override val noteBookBorder: Color = Color(0xFF8D6E63),     // Цвет орехового дерева
    override val iconMicro: DrawableResource = Res.drawable.ic_micro_neon, // Тут стоит использовать темную иконку
    override val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon,

    // Список дел
    override val tintAlarmOn: Color = Color(0xFFF57C00),  // Насыщенный янтарный/оранжевый
    override val tintAlarmOff: Color = Color.Black,  // Приглушенный серо-коричневый
    override val textDesc: Color = Color(0xFF7A6E60),      // Древесно-серый для описаний

    // Контуры карточек (делаем их помягче для светлой темы)
    override val cardItemBorderAlarm: Color = Color(0xFFFBC02D),
    override val cardItemBorderTrue: Color = Color(0xFF81C784),
    override val cardItemBorderFalse: Color = Color(0xFFE57373),

    // Фоны карточек делаем ПАСТЕЛЬНЫМИ (разбеленными). Тёмный текст на них будет выглядеть супер
    override val cardItemAlarm: Color = Color(0xFFFFF9C4), // Мягкий светло-желтый
    override val cardItemTrue: Color = Color(0xFFE8F5E9),  // Нежно-салатовый
    override val cardItemFalse: Color = Color(0xFFFFEBEE), // Приглушенно-розовый

    override val textAlarm: Color = Color(0xFFE65100),     // Темно-оранжевый для цифр времени
    override val chekBoxOff: ImageVector = Icons.Default.CheckBox,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxTint: Color = Color(0xFF5D4037),   // Шоколадный цвет для чекбокса

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF5D4037),
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color(0xFFD32F2F),   // Опасный красный для удаления
    override val iconTint: Color = Color(0xFF5D4037),
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.PlaylistAddCheck,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFF57C00),
    override val tintPremiumOff: Color = Color.Black,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Update,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,

    // Мягкий светлый фон для диалогов (цвет топленого молока или крем-брюле)
    override val backgroundDialog: Color = Color(0xFFF9F6F0),
    override val backgroundStart: DrawableResource = Res.drawable.background_zabor,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_zabor

) : Theme
