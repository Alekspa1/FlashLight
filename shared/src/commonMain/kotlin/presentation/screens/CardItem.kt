package presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
// Если используешь Clock из kotlinx-datetime (в коде у тебя опечатка с kotlin.time, об этом ниже)
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
import androidx.compose.ui.tooling.preview.Preview
import data.room.model.Item
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.Size
import presentation.theme.SizeNormal
import kotlinx.datetime.number
import kotlinx.datetime.DayOfWeek
import presentation.theme.ThemeZabor

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Arrangement

import coil3.compose.AsyncImage
import data.room.model.SubItem
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.BorderStroke
// Для стрелочек раскрытия списка (ошибки на строках 150:77 и 150:112)
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material3.LocalTextStyle

// Для зачеркивания текста выполненной задачи (ошибка на строке 251:86)
import androidx.compose.ui.text.style.TextDecoration

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import org.burnoutcrew.reorderable.*
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.LaunchedEffect
@Composable
fun CardItem(
    item: Item,
    listSubItems: List<SubItem> = emptyList(),
    selectedFileUri: String = "",
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    dragModifier: Modifier = Modifier,
    onClick: (Item, Int) -> Unit = { _, _, -> },
    onSubDragDropped: (List<SubItem>) -> Unit = {} // Колбэк для отправки нового порядка во ViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val subListState = rememberLazyListState()
    
    // ТВОЙ ПОДХОД: Локальный снимок в памяти Compose на основе входящего списка
    var currentSubSnapshotList by remember(listSubItems) { mutableStateOf(listSubItems) }

    // ТВОЙ ПОДХОД: Рубеж защиты — обновляем экран, только если данные в БД реально изменились
    LaunchedEffect(listSubItems) {
        if (currentSubSnapshotList != listSubItems) {
            currentSubSnapshotList = listSubItems
        }
    }

    // ТВОЙ ПОДХОД: Настройка реордера для подзадач
    val subReorderableState = rememberReorderableLazyListState(
        lazyListState = subListState,
        onMove = { from, to ->
            if (from.index in currentSubSnapshotList.indices && to.index in currentSubSnapshotList.indices) {
                val updatedList = currentSubSnapshotList.toMutableList().apply { 
                    add(to.index, removeAt(from.index)) 
                }
                currentSubSnapshotList = updatedList
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... (Твой код Левой кнопки будильника и Центральной карточки дела) ...
        }

        // ВЫЕЗЖАЮЩАЯ КАРТОЧКА С ДРАГ-ЭНД-ДРОПОМ ПО ТВОЕЙ СХЕМЕ
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, top = 6.dp, bottom = 4.dp) 
                    .clip(RoundedCornerShape(15.dp))
                    .border(1.dp, theme.borderCardMenuItem, RoundedCornerShape(15.dp)),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    if (selectedFileUri.isNotEmpty()) {
                        AsyncImage(
                            model = selectedFileUri,
                            contentDescription = "Фото",
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .size(75.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onClick(item, IMAGE) },
                            contentScale = ContentScale.Crop,
                        )
                    }

                    // Переводим подзадачи на LazyColumn фиксированной высоты, чтобы работал Drag-and-Drop
                    LazyColumn(
                        state = subListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp) // Ограничиваем высоту, чтобы список не растягивался бесконечно
                    ) {
                        itemsIndexed(
                            items = currentSubSnapshotList,
                            key = { _, sub -> sub.id }
                        ) { index, subItem ->
                            
                            ReorderableItem(state = subReorderableState, key = subItem.id) { isDragging ->
                                
                                // ТВОЙ ПОДХОД: Формируем чистый dragModifier для подзадачи
                                val subDragHandle = Modifier.longPressDraggableHandle(
                                    enabled = true,
                                    onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                                    onDragStopped = {
                                        // ТВОЙ ПОДХОД: Пересчитываем sort строго в момент отпускания пальца
                                        val listWithUpdatedSort = currentSubSnapshotList.mapIndexed { idx, sub -> 
                                            sub.copy(sort = idx) 
                                        }
                                        currentSubSnapshotList = listWithUpdatedSort // Фиксируем локально
                                        onSubDragDropped(listWithUpdatedSort) // Пушим во ViewModel для Room
                                    }
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(subDragHandle) // Тянуть можно за любую область строки подзадачи
                                        .graphicsLayer { alpha = if (isDragging) 0.5f else 1f }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = subItem.change,
                                        onClick = { onClick(item, CHANGE) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = theme.chekBoxTint,
                                            unselectedColor = theme.borderCardMenuItem
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Text(
                                        text = subItem.name,
                                        color = if (subItem.change) theme.textDesc else theme.textColor,
                                        fontSize = size.textDesc,
                                        style = if (subItem.change) {
                                            val currentStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current
                                            currentStyle.copy(textDecoration = TextDecoration.LineThrough)
                                        } else {
                                            LocalTextStyle.current
                                        },
                                        modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp)
                                    )

                                    IconButton(
                                        onClick = { onClick(item, DELETE) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Удалить подзадачу",
                                            tint = theme.textDesc,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable

// fun CardItem(
//          item: Item,
//          listSubItems : List<SubItem> = emptyList(),
//          selectedFileUri : String = "",    
//          theme: Theme = ThemeNeon(),
//          size: Size = SizeNormal(),  
//          dragModifier: Modifier = Modifier,
//          onClick : (Item, Int) -> Unit = { _, _->},
             
//             ) {
//          var isExpanded by remember { mutableStateOf(false) }

         

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

//     modifier = Modifier
//         .padding(start = 5.dp, end = 5.dp)
//         .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
//         .clip(RoundedCornerShape(15.dp))
//         .border(
//             2.dp,
//             if(item.change) theme.cardItemBorderTrue
//             else if (item.changeAlarm) theme.cardItemBorderAlarm
//             else theme.cardItemBorderFalse,
//             RoundedCornerShape(15.dp)
//         )
//         // 1. Сначала вешаем обычный клик (для открытия диалога)
//         .clickable { onClick(item, CHANGE_ITEM) }
//          .then(dragModifier)
//                      ,
//              shape = RoundedCornerShape(15.dp),

//     colors = CardDefaults.cardColors(
//         containerColor =
//             if(item.change) theme.cardItemTrue
//             else if(item.changeAlarm) theme.cardItemAlarm
//             else theme.cardItemFalse
//     )
// ) {

// Column(){
                              
//                 Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(start = 6.dp, top = 8.dp, bottom = 8.dp,end = 6.dp)
//                     ,
//                     verticalAlignment = Alignment.CenterVertically,


//                 ) {
//                     if (item.uri != "" || listSubItems.isNotEmpty()) IconButton(
//                         onClick = { isExpanded = !isExpanded },
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
//                                 modifier = Modifier.padding(start = 5.dp,top = 8.dp, end = 5.dp),
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


//   // анимированое продолжение
//                                    AnimatedVisibility(

//             visible = isExpanded,

//             enter = expandVertically() + fadeIn(),

//             exit = shrinkVertically() + fadeOut()

//         ) {

//                 Column(
//                     modifier = Modifier.padding(16.dp),
//                     verticalArrangement = Arrangement.spacedBy(8.dp)
//                 ) {
//                                                  AsyncImage(
//                             model = selectedFileUri,
//                             contentDescription = "Превью фото",
//                             modifier = Modifier
//                                 .size(80.dp)
//                                 .clip(RoundedCornerShape(12.dp))
//                                 .clickable { onClick(item,IMAGE) },
//                             contentScale = ContentScale.Crop,
//                         )


//                      listSubItems.forEach{subItem->
//                                                          Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(start = 6.dp, top = 8.dp, bottom = 8.dp,end = 6.dp)
//                     ,
//                     verticalAlignment = Alignment.CenterVertically,


//                 ) {
//                                      Text(
//          modifier = Modifier
//         .padding(start = 5.dp, end = 5.dp)
//         .weight(1f),  
//          text = subItem.name,
//           color = Color.White


//                     )

//                                                          IconButton(
//                         onClick = { },
//                         modifier = Modifier.padding(end = 8.dp).size(24.dp)
//                     ) {

//                         Icon(
//                             modifier = Modifier.fillMaxSize(),

//                             imageVector =
//                            theme.chekBoxOn,
//                             contentDescription = "Chek",
//                             tint = theme.chekBoxTint
//                         )

//                     }
                                     
//                 } 
                     
//                      }                                   

//                 }

//         }
//                            // закончилось анимированое продолжение         
// }


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





// }

// Вспомогательная функция для форматирования времени в строку HH:mm вручную (чтобы не тащить тяжелые форматировщики в commonMain)
private fun formatTime(hour: Int, minute: Int): String {
    val h = if (hour < 10) "0$hour" else "$hour"
    val m = if (minute < 10) "0$minute" else "$minute"
    return "$h:$m"
}

// Вспомогательная функция для ручного форматирования даты dd.MM.yyyy
private fun formatDate(date: LocalDate): String {
    val d = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}"
    val m = if (date.month.number < 10) "0${date.month.number}" else "${date.month.number}"
    return "$d.$m.${date.year}"
}

private fun alarmText(item: Item): String {
    val tz = TimeZone.currentSystemDefault()

    // Явно указываем фабричный метод kotlinx.datetime, чтобы получить правильный тип
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(item.alarmTime)
    val localDateTime = instant.toLocalDateTime(tz)

    val resultTime = formatTime(localDateTime.hour, localDateTime.minute)
    val resultDate = getFormattedDate(item.alarmTime)
    val alarmText = "Напомнит $resultDate в $resultTime"

    return when (item.interval) {
        ALARM_ONE -> alarmText
        ALARM_DAY -> "$alarmText и через день"
        ALARM_WEEK -> "$alarmText и через неделю"
        ALARM_MONTH -> "$alarmText и через месяц"
        ALARM_YEAR -> "$alarmText и через год"
        else -> alarmText
    }
}

private fun getFormattedDate(millis: Long): String {
    val tz = TimeZone.currentSystemDefault()

    // Вместо Clock.System используем обертку ClockSystem из kotlinx-datetime,
    // которая возвращает совместимый тип даты
    val currentMillis = kotlin.time.Clock.System.now().toEpochMilliseconds()

// 2. Переводим их в дату через проверенный kotlinx.datetime.Instant
    val today: LocalDate = kotlinx.datetime.Instant.fromEpochMilliseconds(currentMillis).toLocalDateTime(tz).date

    // Явно создаем целевую дату через правильный пакет
    val targetDate = kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date

    val daysDiff = today.daysUntil(targetDate)

    return when (daysDiff) {
        0 -> "сегодня"
        1 -> "завтра"
        2 -> "послезавтра"
        in 3..6 -> getDayOfWeekWithPreposition(targetDate.dayOfWeek)
        else -> formatDate(targetDate)
    }
}

private fun getDayOfWeekWithPreposition(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "в понедельник"
        DayOfWeek.TUESDAY -> "во вторник"
        DayOfWeek.WEDNESDAY -> "в среду"
        DayOfWeek.THURSDAY -> "в четверг"
        DayOfWeek.FRIDAY -> "в пятницу"
        DayOfWeek.SATURDAY -> "в субботу"
        DayOfWeek.SUNDAY -> "в воскресенье"
    }
}

@Preview
@Composable
fun PrevItem(){
    CardItem(Item(id = 2, name = "Проверить Koin модули", category = "Фокус", alarmTime = 0L, change = false, sort = 2, changeAlarm = true),
        theme = ThemeZabor())
}
