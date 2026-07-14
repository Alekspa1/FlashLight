package presentation.screens

import CommonConst.ALARM
import CommonConst.ALARM_DAY
import CommonConst.ALARM_LONG
import CommonConst.ALARM_MONTH
import CommonConst.ALARM_ONE
import CommonConst.ALARM_WEEK
import CommonConst.ALARM_YEAR
import CommonConst.CHANGE
import CommonConst.CHANGE_ITEM
import CommonConst.DELETE
import CommonConst.IMAGE
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.room.Item
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import kotlinx.datetime.Instant
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.screens.CardItem

@Composable
fun ListToDo(list: List<Item>,
             theme: Theme = ThemeNeon(),
             size : Size = SizeNormal(),
             onClick : (Item, Int) -> Unit = {_,_->},
             onAddItem : () -> Unit = {},
             category : String = "Тест"
             ){
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(

            modifier = Modifier.fillMaxWidth().weight(1f),

            verticalArrangement = Arrangement.spacedBy(5.dp),

        ) {
            val categoryName = category
            item(key = categoryName) {
                Box(modifier = Modifier.fillMaxWidth()){
                    Text(
                        text = categoryName,
                        color = theme.textColor,
                        fontSize = size.textMenu,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            items(
                items = list,
                key = { it.id}
            ){item->
                Box(modifier = Modifier.animateItem()) {
                    CardItem(item = item,
                        theme = theme,
                        size = size) { item, action -> onClick(item, action)
                    }
                }
            }

        }



        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(8.dp)

            ){
                IconButton(modifier = Modifier.size(50.dp).align(Alignment.Center),
                    onClick = {  },
                ){
                    Image(
                        painter = painterResource(theme.iconMicro),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()

                        )
                }

                IconButton(modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
                    onClick = { onAddItem() },
                ){
                    Icon(
                        imageVector = theme.iconAdd,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = theme.iconAddTint

                    )
                }
            }

        }
    }




}

// @Composable

// fun Item(item: Item,
//          theme: Theme = ThemeNeon(),
//          size: Size = SizeNormal(),
//          onClick : (Item, Int) -> Unit = { _, _->}) {

//         Row(
//             modifier = Modifier.fillMaxWidth(),
//             verticalAlignment = Alignment.CenterVertically // Все три элемента будут идеально ровно по центру высоты
//         ) {
//             // 1. Левая кнопка/текст

//                 Box(
//                     modifier = Modifier
//                         .padding(start = 8.dp)
//                         .size(35.dp).combinedClickable(
//                         onClick = {onClick(item,ALARM)},
//                         onLongClick = {onClick(item,ALARM_LONG)}
//                     )



//                 ) {

//                     Icon(
//                         imageVector = Icons.Default.Alarm, // Нужен импорт androidx.compose.material.icons.Icons
//                         contentDescription = "Будильник",
//                        tint =  if(item.changeAlarm) theme.tintAlarmOn else theme.tintAlarmOff,
//                         modifier = Modifier.fillMaxSize(),
//                     )

//                 }


//             // 2. Центральная карточка (занимает всё оставшееся пространство)

//             Card(
//                 modifier = Modifier
//                     .padding(start = 5.dp, end = 5.dp)
//                     .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
//                     .clip(RoundedCornerShape(15.dp))
//                     .border(
//                         2.dp,
//                         if(item.change) theme.cardItemBorderTrue
//                         else if (item.changeAlarm) theme.cardItemBorderAlarm
//                         else theme.cardItemBorderFalse,
//                         RoundedCornerShape(15.dp)

//                     ).clickable{onClick(item,CHANGE_ITEM)}
//                     ,

//                 shape = RoundedCornerShape(16.dp),

//                 colors = CardDefaults.cardColors(
//                     containerColor =
//                         if(item.change) theme.cardItemTrue
//                         else if(item.changeAlarm) theme.cardItemAlarm
//                         else theme.cardItemFalse
//                 )
//             ) {
//                 Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(start = 6.dp, top = 8.dp, bottom = 8.dp,end = 6.dp)
//                     ,
//                     verticalAlignment = Alignment.CenterVertically,


//                 ) {
//                     if (item.uri != "") IconButton(
//                         onClick = { onClick(item,IMAGE) },
//                         modifier = Modifier.padding(start = 8.dp).size(24.dp)

//                     ) {

//                         Icon(
//                             modifier = Modifier.fillMaxSize(),
//                             imageVector = theme.iconImage, // Нужен импорт androidx.compose.material.icons.Icons
//                             contentDescription = "Картинка",
//                             tint = theme.iconTint
//                         )

//                     }

//                     Column(
//                         modifier = Modifier
//                             .weight(1f)
//                             .padding(start = 6.dp, end = 6.dp),
//                     ) {
//                         Text(
//                             modifier = Modifier.padding(start = 5.dp,end = 5.dp),
//                             text = item.name,
//                             color = theme.textColor,
//                             lineHeight = size.lineHeightItem,
//                             fontSize = size.textItem)
//                         if (item.desc.isNotEmpty())  {
//                             Text(
//                                 modifier = Modifier.padding(start = 5.dp,top = 2.dp, end = 5.dp),
//                                 text = item.desc,
//                                 color = theme.textDesc,
//                                 lineHeight = size.lineHeightDescAndAlarm,
//                                 fontSize = size.textDesc) }
//                         if (item.changeAlarm) {
//                             Text(
//                                 modifier = Modifier.padding(start = 5.dp,top = 4.dp, end = 5.dp),
//                                 text = alarmText(item),
//                                 color = theme.textAlarm,
//                                 fontSize = size.textAlarm,
//                                 lineHeight = size.lineHeightDescAndAlarm
//                             )
//                         }
//                     }
//                     IconButton(
//                         onClick = { onClick(item,CHANGE) },
//                         modifier = Modifier.padding(end = 8.dp).size(24.dp)
//                     ) {

//                         Icon(
//                             modifier = Modifier.fillMaxSize(),

//                             imageVector =
//                                 if(item.change)  theme.chekBoxOn
//                                 else theme.chekBoxOff,
//                             contentDescription = "Chek",
//                             tint = theme.chekBoxTint
//                         )

//                     }
//                 }
//             }



//             // 3. Правая кнопка/текст



//                 IconButton(

//                     onClick = {onClick(item,DELETE)  },
//                     modifier = Modifier.padding(end = 8.dp).size(35.dp)


//                 ) {

//                     Icon(
//                         modifier = Modifier.fillMaxSize(),
//                         imageVector = theme.iconDelItem, // Нужен импорт androidx.compose.material.icons.Icons
//                         contentDescription = "Меню",
//                         tint = theme.iconDelTint,

//                     )

//                 }



//         } // Конец Row





}

// // Вспомогательная функция для форматирования времени в строку HH:mm вручную (чтобы не тащить тяжелые форматировщики в commonMain)
// private fun formatTime(hour: Int, minute: Int): String {
//     val h = if (hour < 10) "0$hour" else "$hour"
//     val m = if (minute < 10) "0$minute" else "$minute"
//     return "$h:$m"
// }

// // Вспомогательная функция для ручного форматирования даты dd.MM.yyyy
// private fun formatDate(date: LocalDate): String {
//     val d = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}"
//     val m = if (date.month.number < 10) "0${date.month.number}" else "${date.month.number}"
//     return "$d.$m.${date.year}"
// }

// fun alarmText(item: Item): String {
//     val tz = TimeZone.currentSystemDefault()

//     // Явно указываем фабричный метод kotlinx.datetime, чтобы получить правильный тип
//     val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(item.alarmTime)
//     val localDateTime = instant.toLocalDateTime(tz)

//     val resultTime = formatTime(localDateTime.hour, localDateTime.minute)
//     val resultDate = getFormattedDate(item.alarmTime)
//     val alarmText = "Напомнит $resultDate в $resultTime"

//     return when (item.interval) {
//         ALARM_ONE -> alarmText
//         ALARM_DAY -> "$alarmText и через день"
//         ALARM_WEEK -> "$alarmText и через неделю"
//         ALARM_MONTH -> "$alarmText и через месяц"
//         ALARM_YEAR -> "$alarmText и через год"
//         else -> alarmText
//     }
// }

// private fun getFormattedDate(millis: Long): String {
//     val tz = TimeZone.currentSystemDefault()

//     // Вместо Clock.System используем обертку ClockSystem из kotlinx-datetime,
//     // которая возвращает совместимый тип даты
//     val currentMillis = kotlin.time.Clock.System.now().toEpochMilliseconds()

// // 2. Переводим их в дату через проверенный kotlinx.datetime.Instant
//     val today: LocalDate = kotlinx.datetime.Instant.fromEpochMilliseconds(currentMillis).toLocalDateTime(tz).date

//     // Явно создаем целевую дату через правильный пакет
//     val targetDate = kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date

//     val daysDiff = today.daysUntil(targetDate)

//     return when (daysDiff) {
//         0 -> "сегодня"
//         1 -> "завтра"
//         2 -> "послезавтра"
//         in 3..6 -> getDayOfWeekWithPreposition(targetDate.dayOfWeek)
//         else -> formatDate(targetDate)
//     }
// }

// private fun getDayOfWeekWithPreposition(dayOfWeek: DayOfWeek): String {
//     return when (dayOfWeek) {
//         DayOfWeek.MONDAY -> "в понедельник"
//         DayOfWeek.TUESDAY -> "во вторник"
//         DayOfWeek.WEDNESDAY -> "в среду"
//         DayOfWeek.THURSDAY -> "в четверг"
//         DayOfWeek.FRIDAY -> "в пятницу"
//         DayOfWeek.SATURDAY -> "в субботу"
//         DayOfWeek.SUNDAY -> "в воскресенье"
//     }
// }

@Preview(showBackground = true)
@Composable
fun ToDoListPreview() {
    // Наш фейковый список из 10 элементов для Студии
    val mockList = listOf(
        Item(id = 1, name = "Купить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую ленту", category = "Фокус", alarmTime = 1719750000000L, change = false, sort = 1),
        Item(id = 2, name = "Проверить Koin модули", category = "Фокус", alarmTime = 0L, change = false, sort = 2),
        Item(id = 3, name = "Починить затыки Skia на Windows", category = "Фокус", alarmTime = 1719753600000L, change = true, sort = 3, uri = "трололо", changeAlarm = false, desc = "Описание почему то может быть null"),
        Item(id = 4, name = "Похвалить себя за архитектуру", category = "Фокус", alarmTime = 0L, change = false, sort = 4),
        Item(id = 5, name = "Выпить кофе и размять спину", category = "Фокус", alarmTime = 0L, change = false, sort = 5),
        Item(id = 6, name = "Написать expect/actual для iOS", category = "Фокус", alarmTime = 1719760800000L, change = false, sort = 6, changeAlarm = true),
        Item(id = 7, name = "Протестировать Drag-and-Drop", category = "Фокус", alarmTime = 0L, change = true, sort = 7),
        Item(id = 8, name = "Удалить лишние .value из Flow", category = "Фокус", alarmTime = 0L, change = false, sort = 8),
        Item(id = 9, name = "Развернуть базу Room на десктопе", category = "Фокус", alarmTime = 1719771600000L, change = false, sort = 9),
        Item(id = 10, name = "Устроить киберпанк в интерфейсе", category = "Фокус", alarmTime = 0L, change = false, sort = 10)
    )

    // Вызываем твой экран списков и скармливаем ему этот муляж
    ListToDo(list = mockList)
}


