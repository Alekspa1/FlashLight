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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_drawer_neon
import flashlight.shared.generated.resources.background_drawer_poison
import flashlight.shared.generated.resources.background_groza
import flashlight.shared.generated.resources.background_mramor
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_platina
import flashlight.shared.generated.resources.background_poison
import flashlight.shared.generated.resources.background_vulcan
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.background_drawer_vulcan
import org.jetbrains.compose.resources.DrawableResource


sealed interface Theme{
    val backgroundStart : DrawableResource
    val backgroundDrawer : DrawableResource
    val backgroundCalendar : Color
    val noteBookBackground: Color
    val noteBookBorder: Color
    val textColor: Color
    val iconMicro : ImageVector
    val iconDel : ImageVector
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
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,
    //Блокнот
    override  val noteBookBackground: Color = Color(0x9900BCD4),
    override  val noteBookBorder: Color = Color(0x9900E2FF),
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
    override val colorCalendarDaySelect: Color = Color.Black,
    override val backgroundCalendar: Color = Color.Transparent


    //Дравер

) : Theme

data class ThemeZabor (
    override val textColor: Color = Color.Black,

    override val noteBookBackground: Color = Color(0x7FFFEB3B),
    override val noteBookBorder: Color = Color(0xFF8D6E63),
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,

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
    override val colorCalendarDaySelect: Color = Color.White,
    override val backgroundCalendar: Color = Color.Transparent

) : Theme


data class ThemeStorm(
    override val textColor: Color = Color(0xFFFFFFFF),          // Чистый белый для максимальной читаемости
    override val iconMicro: ImageVector = Icons.Default.Mic,
    override val iconDel: ImageVector = Icons.Default.Delete,

    override val noteBookBackground: Color = Color(0xFF12121E), // Почти чёрный, глубокий индиго
    override val noteBookBorder: Color = Color(0x807B21FF),    // Полупрозрачный яркий фиолетовый

    override val tintAlarmOn: Color = Color(0xFF7B21FF),        // Акцентный фиолетовый (цвет молнии)
    override val tintAlarmOff: Color = Color(0xFF6F6F6F),
    override val textDesc: Color = Color(0xFFB3C8FF),           // Светло‑голубой для описаний (как отсвет молнии)

    override val cardItemBorderAlarm: Color = Color(0xFF7B21FF),
    override val cardItemBorderTrue: Color = Color(0x8000FF87),
    override val cardItemBorderFalse: Color = Color(0x99D50000),

    override val cardItemAlarm: Color = Color(0xEC1A1A28),      // Тёмно‑фиолетовый фон карточки с тревожным оттенком
    override val cardItemTrue: Color = Color(0xEC09140E),
    override val cardItemFalse: Color = Color(0xF23D0101),

    override val textAlarm: Color = Color(0xFFFFD700),          // Золотой для времени — как вспышка перед разрядом (альтернатива: оставить белым)
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color(0xFF7B21FF),        // Фиолетовые чекбоксы

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFFFFD700),         // Золотой плюс — точка фокуса
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFFFD700),      // Золотой для премиум — выделяется на фиолетовом
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF12121E),   // Тёмный фон для диалогов
    override val backgroundStart: DrawableResource = Res.drawable.background_groza, // Твой фон с молнией
    override val backgroundDrawer: DrawableResource = Res.drawable.background_groza,
    override val borderCardMenuItem: Color = Color(0x807B21FF),
    override val cardMenuItem: Color = Color(0xE61A1A28),        // Тёмно‑фиолетовый фон пунктов меню
    override val colorCalendarDaySelect: Color = Color(0xFF7B21FF), // Фиолетовый для выбранного дня
    override val backgroundCalendar: Color = noteBookBackground,
) : Theme


data class ThemeMarble (
    override val textColor: Color = Color(0xFF1C1D22),           // Глубокий антрацитовый (почти черный) для отличной читаемости на белом

    // Блокнот (Календарь)
    override val noteBookBackground: Color = Color(0xF2F4F5F7), // Мягкий ультра-светлый серый (основа мрамора, 95% плотности)
    override val noteBookBorder: Color = Color(0x99A0A5B0),     // Цвет серых прожилок для аккуратного контура
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,

    // Список дел
    override val tintAlarmOn: Color = Color(0xFF4A5060),        // Активный будильник глубокого стального/графитового цвета
    override val tintAlarmOff: Color = Color(0xFFB0B5C0),       // Выключенный будильник (светло-стальной)
    override val textDesc: Color = Color(0xFF5A6070),           // Сдержанный графитовый для описания задач


    override val cardItemBorderAlarm: Color = Color(0xFF4A5060), // Строгий графитовый бордюр для важных задач
    override val cardItemBorderTrue: Color = Color(0xFF2E7D32),  // Спокойный благородный зеленый бордюр (без неона)
    override val cardItemBorderFalse: Color = Color(0xFFC62828), // Сдержанный рубиново-красный бордюр

    override val cardItemAlarm: Color = Color(0xF2FFFFFF),       // Чистый белый фон карточки с высокой плотностью (95%), чтобы отрываться от узора фона
    override val cardItemTrue: Color = Color(0xF2F1F8E9),        
    override val cardItemFalse: Color = Color(0xF2FFEBEE),       

    override val textAlarm: Color = Color(0xFF4A5060),           // Графитовый текст времени
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color(0xFF1C1D22),

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF1C1D22),         // Черная строгая кнопка добавления задач
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color(0xFF4A5060),
    override val iconTint: Color = Color(0xFF4A5060),
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFF1C1D22),       // Премиальная глянцево-черная корона вместо желтой
    override val tintPremiumOff: Color = Color(0xFFB0B5C0),
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFFFFFFFF),    // Белоснежный фон системных окон и диалогов
    override val backgroundStart: DrawableResource = Res.drawable.background_mramor, 
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon, 
    override val borderCardMenuItem: Color = Color(0xCCCFD2D9),  // Мягкая серебристая рамка пунктов меню
    override val cardMenuItem: Color = Color(0xF2F4F5F7),        // Светлая подложка для пунктов настроек
    override val colorCalendarDaySelect: Color = Color.White,
    override val backgroundCalendar: Color = Color.Transparent
) : Theme


 data class ThemePoison (

     override val textColor: Color = Color(0xFFE0F7FA),
     // Очень светлый, почти белый мятный оттенок для максимального контраста
     override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
     override val iconDel: ImageVector = Icons.Default.Delete,
     // Блокнот
     override val noteBookBackground: Color = Color(0xCC0B140F),
     override val noteBookBorder: Color = Color(0x8000FF87),

     // Список дел
     override val tintAlarmOn: Color = Color(0xFF00FF87),
     override val tintAlarmOff: Color = Color(0xFF6F6F6F),
     override val textDesc: Color = Color(0xFFA7FFEB),


     override val cardItemBorderAlarm: Color = Color(0xFF00FF87),
     override val cardItemBorderTrue: Color = Color(0x8000FF87),
     override val cardItemBorderFalse: Color = Color(0x99D50000),

     override val cardItemAlarm: Color = Color(0xEC09140E),
     override val cardItemTrue: Color = Color(0xEC09140E),
     override val cardItemFalse: Color = Color(0xF23D0101),

     override val textAlarm: Color = Color(0xFF00FF87),           // Светящийся зеленый текст времени
     override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
     override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
     override val chekBoxTint: Color = Color.White,         // Яркий токсичный чекбокс

     override val iconImage: ImageVector = Icons.Default.Image,
     override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
     override val iconAddTint: Color = Color(0xFFE2FFE9),
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
     override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_poison, // Оставлено как есть
     override val borderCardMenuItem: Color = Color(0x8000FF87),
     override val cardMenuItem: Color = Color(0xE6121D15),         // Фон меню цвета старого темного чугуна
     override val colorCalendarDaySelect: Color = Color.Black,
     override val backgroundCalendar: Color = noteBookBackground,

     ) : Theme

 data class ThemeVolcanic (
     override val textColor: Color = Color(0xFFFFE0B2),           // Мягкий тепло-оранжевый (цвет остывающего пепла)

     // Блокнот (Календарь)
     override val noteBookBackground: Color = Color(0xDC0F0E12), // Плотный базальтово-черный цвет (86% плотности)
     override val noteBookBorder: Color = Color(0xFFFF6D00),     // Насыщенный огненно-оранжевый контур магмы
     override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
     override val iconDel: ImageVector = Icons.Default.Delete,

     // Список дел
     override val tintAlarmOff: Color = Color(0xFF90A4AE),       // Активный будильник горит цветом раскаленной лавы
     override val tintAlarmOn: Color = Color(0xFFFF6D00),       // Потухший уголь (выключен)
     override val textDesc: Color = Color(0xFF9E9A9F),           // Дымчато-серый пепельный для описания задач


     override val cardItemBorderAlarm: Color = Color(0xFFFF3D00), // Красно-оранжевый бордюр для важных задач
     override val cardItemBorderTrue: Color = Color(0xFF00E676),  // Чистый зеленый бордюр
     override val cardItemBorderFalse: Color = Color(0xFFFF1744), // Чистый красный бордюр

     override val cardItemAlarm: Color = Color(0xDA0B0A0D),       // Глубокий угольный
     override val cardItemFalse: Color = Color(0xDA1A0B0B),
     override val cardItemTrue: Color = Color(0xE1014E29),


     override val textAlarm: Color = Color(0xFFFF6D00),           // Огненный текст времени
     override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
     override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
     override val chekBoxTint: Color = Color(0xFFFFFFFF),        // Чекбокс светится оранжевым пламенем

     override val iconImage: ImageVector = Icons.Default.Image,
     override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
     override val iconAddTint: Color = Color(0xFFFF6D00),         // Кнопка добавления горит цветом магмы
     override val iconDelItem: ImageVector = Icons.Default.Delete,
     override val iconDelTint: Color = Color.White,
     override val iconTint: Color = Color.White,
     override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
     override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
     override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
     override val tintPremiumOn: Color = Color(0xFFFF6D00),       // Огненная корона премиума
     override val tintPremiumOff: Color = Color.White,
     override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
     override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
     override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
     override val backgroundDialog: Color = Color(0xFF0B0A0D),    // Базальтово-черный фон системных окон и диалогов
     override val backgroundStart: DrawableResource = Res.drawable.background_vulcan,
     override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_vulcan,
     override val borderCardMenuItem: Color = Color(0x80FF6D00),  // Рамка меню цвета лавовой реки
     override val cardMenuItem: Color = Color(0xE60F0E12),        // Плотный фон пунктов настроек
     override val colorCalendarDaySelect: Color = Color.Black,
     override val backgroundCalendar: Color = noteBookBackground      // Черная цифра внутри огненного круга выделения
 ) : Theme


data class ThemePlatinum (
    override val textColor: Color = Color(0xFF0F172A),           // Глубокий космический темно-синий

    // Блокнот (Календарь)
    override val noteBookBackground: Color = Color(0xF2E2E8F0), // Матовая платиновая сталь (95% плотности)
    override val noteBookBorder: Color = Color(0x9994A3B8),     // Холодный серебряный контур
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,

    // Список дел
    override val tintAlarmOn: Color = Color(0xFF334155),        // Активный будильник цвета темной оружейной стали
    override val tintAlarmOff: Color = Color(0xFF94A3B8),       // Выключенный будильник (матовое серебро)
    override val textDesc: Color = Color(0xFF475569),           // Сдержанный стальной сине-серый для описания задач


    override val cardItemBorderAlarm: Color = Color(0xFF334155), // Строгий темно-стальной бордюр для важных задач
    override val cardItemBorderTrue: Color = Color(0xFF15803D),  // Благородный изумрудно-зеленый бордюр
    override val cardItemBorderFalse: Color = Color(0xFFB91C1C), // Глубокий рубиново-красный бордюр

    override val cardItemAlarm: Color = Color(0xF2F8FAFC),       // Ультра-светлый зеркальный фон карточки (95% плотности)
    override val cardItemTrue: Color = Color(0xF2F0FDF4),        
    override val cardItemFalse: Color = Color(0xF2FEF2F2),       

    override val textAlarm: Color = Color(0xFF334155),           // Стальной текст времени
    override val chekBoxOff: ImageVector = Icons.Default.CheckBoxOutlineBlank,
    override val chekBoxOn: ImageVector = Icons.Default.CheckBox,
    override val chekBoxTint: Color = Color(0xFF0F172A),         // Контрастный темно-синий чекбокс

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF0F172A),         // Строгая темно-синяя кнопка добавления
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color(0xFF475569),
    override val iconTint: Color = Color(0xFF475569),
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFF0F172A),       // Глянцевая темно-синяя корона премиума
    override val tintPremiumOff: Color = Color(0xFF94A3B8),
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFFFFFFFF),    // Чистый белый фон для системных окон
    override val backgroundStart: DrawableResource = Res.drawable.background_platina, // Оставлено как есть, под твою замену
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon, // Оставлено как есть, под твою замену
    override val borderCardMenuItem: Color = Color(0xCCCBD5E1),  // Мягкая рамка пунктов меню
    override val cardMenuItem: Color = Color(0xF2E2E8F0),        // Матовая подложка для настроек
    override val colorCalendarDaySelect: Color = Color.White,
    override val backgroundCalendar: Color = noteBookBackground      // Белая цифра внутри выделенного дня
) : Theme
