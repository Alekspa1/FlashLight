package presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_drawer_neon
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_poison
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.ic_del_notebook_neon
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


sealed interface Theme{
    val backgroundStart : DrawableResource
    val backgroundDrawer : DrawableResource
    val textColor: Color
    val noteBookBackground: Color
    val noteBookBorder: Color
    @Composable
    fun iconMicro() : Painter
    @Composable
    fun iconDel() : Painter
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
    val borderCardMenuItem: Color
    val cardMenuItem : Color
    val colorCalendarDaySelect : Color
}

data class ThemeNeon (
    override val textColor: Color = Color.White,

    //Блокнот
    override  val noteBookBackground: Color = Color(0x9900BCD4),
    override  val noteBookBorder: Color = Color(0x9900E2FF),
    val iconMicro: DrawableResource = Res.drawable.ic_micro_neon,
    val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon,

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
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon,
    override val borderCardMenuItem: Color = Color(0x9900E2FF),
    override val cardMenuItem: Color = Color(0x6500BCD4),
    override val colorCalendarDaySelect: Color = Color.Black


    //Дравер

) : Theme {
    @Composable
    override fun iconMicro(): Painter {
        return painterResource(iconMicro)
    }

    @Composable
    override fun iconDel(): Painter {
        return painterResource(iconDel)
    }
}

data class ThemeZabor (
    override val textColor: Color = Color.Black,

    override val noteBookBackground: Color = Color(0x7FFFEB3B),
    override val noteBookBorder: Color = Color(0xFF8D6E63),
    val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    val iconDel: ImageVector = Icons.Default.Delete,

    // Список дел
    override val tintAlarmOn: Color = Color.Yellow,  // Насыщенный янтарный/оранжевый
    override val tintAlarmOff: Color = Color.Black,  // Приглушенный серо-коричневый
    override val textDesc: Color = Color.Black,      // Древесно-серый для описаний

    // Контуры карточек (делаем их помягче для светлой темы)
    override val cardItemBorderAlarm: Color = Color(0x7FFFEB3B),
    override val cardItemBorderTrue: Color = Color(0x6623BD00),
    override val cardItemBorderFalse: Color = Color(0xB3FB4141),

    // Фоны карточек делаем ПАСТЕЛЬНЫМИ (разбеленными). Тёмный текст на них будет выглядеть супер
    override val cardItemAlarm: Color = Color(0xB3D6C000), // Мягкий светло-желтый
    override val cardItemTrue: Color = Color(0xFF6623BD00),  // Нежно-салатовый
    override val cardItemFalse: Color = Color(0x80F30404), // Приглушенно-розовый

    override val textAlarm: Color = Color.Yellow,     // Темно-оранжевый для цифр времени
    override val chekBoxOff: ImageVector =  Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color.Black,   // Шоколадный цвет для чекбокса

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color.Black,
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.Black,   // Опасный красный для удаления
    override val iconTint: Color = Color.Black,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color.Yellow,
    override val tintPremiumOff: Color = Color.Black,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Update,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,

    // Мягкий светлый фон для диалогов (цвет топленого молока или крем-брюле)
    override val backgroundDialog: Color = Color(0xFFF9F6F0),
    override val backgroundStart: DrawableResource = Res.drawable.background_zabor,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_zabor,
    override val borderCardMenuItem: Color = Color(0xFF5E5F61),
    override val cardMenuItem: Color = Color(0x99B6B6B6),
    override val colorCalendarDaySelect: Color = Color.White

) : Theme {
    @Composable
    override fun iconMicro(): Painter {
       return rememberVectorPainter(iconMicro)
    }

    @Composable
    override fun iconDel(): Painter {
        return rememberVectorPainter(iconDel)
    }
}


data class ThemeStorm (
    override val textColor: Color = Color.White,

    // Блокнот
    override val noteBookBackground: Color = Color(0x99311B92), // Глубокий фиолетовый
    override val noteBookBorder: Color = Color(0x9900E5FF),     // Электрический синий (вспышка)
    val iconMicro: DrawableResource = Res.drawable.ic_micro_neon, // Оставлено как есть
    val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon, // Оставлено как есть

    // Список дел
    override val tintAlarmOn: Color = Color(0xFFFFEB3B),        // Ярко-желтая молния
    override val tintAlarmOff: Color = Color(0xFFB0BEC5),       // Грозовое облако
    override val textDesc: Color = Color(0xFFCFD8DC),           // Светло-серый дым


    override val cardItemBorderAlarm: Color = Color(0xB3FFD54F), // Приглушенный желтый
    override val cardItemBorderTrue: Color = Color(0xB300E676),  // Зеленая вспышка
    override val cardItemBorderFalse: Color = Color(0xB3FF1744), // Красная вспышка

    override val cardItemAlarm: Color = Color(0x801A237E),       // Полупрозрачный ночной синий
    override val cardItemTrue: Color = Color(0x4D1B5E20),        // Темно-зеленый
    override val cardItemFalse: Color = Color(0x4DB71C1C),       // Темно-красный

    override val textAlarm: Color = Color(0xFFFFEB3B),           // Желтый текст будильника
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color.White,

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF90CAF9),
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFFFD700),
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF212121),    // Очень темный серый для окон
    override val backgroundStart: DrawableResource = Res.drawable.background_neon, // Оставлено как есть
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon, // Оставлено как есть
    override val borderCardMenuItem: Color = Color(0x997C4DFF),  // Фиолетовая рамка меню
    override val cardMenuItem: Color = Color(0x65283593),        // Сине-фиолетовое меню
    override val colorCalendarDaySelect: Color = Color.Black
) : Theme {
    @Composable
    override fun iconMicro(): Painter = painterResource(iconMicro)

    @Composable
    override fun iconDel(): Painter = painterResource(iconDel)
}


data class ThemeMarble (
    override val textColor: Color = Color(0xFF1A1A1A),           // Глубокий антрацитовый (почти черный) для идеальной читаемости

    // Блокнот
    override val noteBookBackground: Color = Color(0xFAF5F5F5), // Мягкий ультра-светлый серый (цвет основы мрамора)
    override val noteBookBorder: Color = Color(0x99757575),     // Цвет серых прожилок для контуров
    val iconMicro: DrawableResource = Res.drawable.ic_micro_neon, // Оставлено как есть
    val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon, // Оставлено как есть

    // Список дел
    override val tintAlarmOn: Color = Color(0xFF424242),        // Строгий графитовый для активного будильника
    override val tintAlarmOff: Color = Color(0xFFBDBDBD),       // Светло-стальной для выключенного
    override val textDesc: Color = Color(0xFF616161),           // Благородный серый для второстепенного текста


    override val cardItemBorderAlarm: Color = Color(0xB3757575), // Графитовый бордюр
    override val cardItemBorderTrue: Color = Color(0xB32E7D32),  // Спокойный, глубокий зеленый бордюр (без неона)
    override val cardItemBorderFalse: Color = Color(0xB3C62828), // Сдержанный благородный красный бордюр

    override val cardItemAlarm: Color = Color(0xFFFFFFFF),       // Чистый белый фон карточки (чтобы выделялась на фоне приложения)
    override val cardItemTrue: Color = Color(0xFFF1F8E9),        // Очень тонкий, едва заметный пастельно-зеленый оттенок
    override val cardItemFalse: Color = Color(0xFFFFEBEE),       // Очень тонкий пастельно-красный оттенок

    override val textAlarm: Color = Color(0xFF424242),           // Темный графитовый текст времени
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color(0xFF212121),         // Контрастный черный чекбокс

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF212121),         // Строгая черная кнопка добавления
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color(0xFF424242),
    override val iconTint: Color = Color(0xFF424242),
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFF1A1A1A),       // Премиальная черная глянец-корона вместо желтой
    override val tintPremiumOff: Color = Color(0xFF9E9E9E),
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFFFFFFFF),    // Белоснежное окно диалога
    override val backgroundStart: DrawableResource = Res.drawable.background_neon, // Оставлено как есть
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon, // Оставлено как есть
    override val borderCardMenuItem: Color = Color(0xCCBDBDBD),  // Тонкая стальная рамка меню
    override val cardMenuItem: Color = Color(0x65F5F5F5),        // Полупрозрачный белый элемент меню
    override val colorCalendarDaySelect: Color = Color.White     // Белый текст на выделенном дне
) : Theme {
    @Composable
    override fun iconMicro(): Painter = painterResource(iconMicro)

    @Composable
    override fun iconDel(): Painter = painterResource(iconDel)
}


data class ThemePoison (
    override val textColor: Color = Color(0xFFE0F7FA),           // Очень светлый, почти белый мятный оттенок для максимального контраста

    // Блокнот
    override val noteBookBackground: Color = Color(0xCC0B140F), 
    override val noteBookBorder: Color = Color(0x8000FF87),
    val iconMicro: DrawableResource = Res.drawable.ic_micro_neon, // Оставлено как есть
    val iconDel: DrawableResource = Res.drawable.ic_del_notebook_neon, // Оставлено как есть

    // Список дел
    override val tintAlarmOn: Color = Color(0xFF00FF87),        // Токсично-зеленый светящийся активный будильник
    override val tintAlarmOff: Color = Color(0xFF455A64),       // Тусклый металлическо-серый для выключенного
    override val textDesc: Color = Color(0xFFA7FFEB),          // Серый с зеленым отливом для описания


    override val cardItemBorderAlarm: Color = Color(0xFF00FF87), // Ядовито-зеленый для важных/активных
    override val cardItemBorderTrue: Color = Color(0xFF00E676),  // Насыщенный зеленый для выполненных
    override val cardItemBorderFalse: Color = Color(0xFFFF1744), // Насыщенный красный для просроченных/отмененных

    override val cardItemAlarm: Color = Color(0xDA0B140F),
    override val cardItemTrue: Color = Color(0xDA0A1A0E),
    override val cardItemFalse: Color = Color(0xDA1A0A0A),

    override val textAlarm: Color = Color(0xFF00FF87),           // Светящийся зеленый текст времени
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color(0xFF00FF87),         // Яркий токсичный чекбокс

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF00FF87),         // Кнопка добавления горит зеленым огнем
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFFFEB3B),       // Золотая корона (как золотые элементы на этикетке Poison)
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF1A231F),    // Очень темный графитово-зеленый цвет для окон
    override val backgroundStart: DrawableResource = Res.drawable.background_poison, // Оставлено как есть
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon, // Оставлено как есть
    override val borderCardMenuItem: Color = Color(0x8000FF87),  
    override val cardMenuItem: Color = Color(0xE6121D15),         // Фон меню цвета старого темного чугуна
    override val colorCalendarDaySelect: Color = Color.Black     // Черный текст внутри ярко-зеленого круга выделения
) : Theme {
    @Composable
    override fun iconMicro(): Painter = painterResource(iconMicro)

    @Composable
    override fun iconDel(): Painter = painterResource(iconDel)
}
