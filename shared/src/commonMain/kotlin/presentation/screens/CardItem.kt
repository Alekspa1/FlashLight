package presentation.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.heightIn // ДОБАВИЛИ: Ограничитель высоты для вложенного LazyColumn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // ДОБАВИЛИ: Важный триггер синхронизации данных
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalHapticFeedback // ДОБАВИЛИ: Вызов вибрации мотора смартфона
import androidx.compose.ui.hapticfeedback.HapticFeedbackType // ДОБАВИЛИ: Определение типа вибрации (LongPress)
import coil3.compose.AsyncImage

// ВОССТАНОВИЛИ: Полный набор библиотек времени для твоих методов расчета дат внизу файла
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.DayOfWeek

// ВОССТАНОВИЛИ: Все до единой константы алармов, которые ругались в логе компиляции
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

// Данные и темы проекта
import data.room.model.Item
import data.room.model.SubItem
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.ThemeZabor
import presentation.theme.Size
import presentation.theme.SizeNormal

import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@Composable
fun CardItem(
    item: Item,
    listSubItems: List<SubItem> = emptyList(),
    selectedFileUri: String = "",
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    dragModifier: Modifier = Modifier,
    onSubDragDropped: (List<SubItem>) -> Unit = {},
    onClick: (Item, Int) -> Unit = { _, _ -> }, // 2 аргумента для главных дел
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current


    var currentSnapshotList by remember { mutableStateOf<List<SubItem>>(listSubItems) }

    LaunchedEffect(listSubItems) {
        if (currentSnapshotList != listSubItems) {
            currentSnapshotList = listSubItems
        }
    }

        val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState
            val fromIdx = from.index - 1
            val toIdx = to.index - 1
            if (fromIdx in currentSnapshotList.indices && toIdx in currentSnapshotList.indices) {
                val updatedList = currentSnapshotList.toMutableList().apply { add(toIdx, removeAt(fromIdx)) }
                currentSnapshotList = updatedList
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Левая кнопка (Будильник)
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(35.dp)
                    .combinedClickable(
                        onClick = { onClick(item, ALARM) },
                        onLongClick = { onClick(item, ALARM_LONG) }
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Будильник",
                    tint = if (item.changeAlarm) theme.tintAlarmOn else theme.tintAlarmOff,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // 2. Центральная ОСНОВНАЯ карточка
            Card(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        2.dp,
                        if (item.change) theme.cardItemBorderTrue 
                        else if (item.changeAlarm) theme.cardItemBorderAlarm 
                        else theme.cardItemBorderFalse,
                        RoundedCornerShape(15.dp)
                    )
                    .clickable { onClick(item, CHANGE_ITEM) }
                    .then(dragModifier),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.change) theme.cardItemTrue 
                    else if (item.changeAlarm) theme.cardItemAlarm 
                    else theme.cardItemFalse
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Иконка стрелочки-спойлера
                    if (item.uri.isNotEmpty() || listSubItems.isNotEmpty()) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.padding(start = 4.dp).size(24.dp)
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Раскрыть",
                                tint = theme.iconTint
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp, end = 6.dp),
                    ) {
                        Text(
                            text = item.name,
                            color = theme.textColor,
                            lineHeight = size.lineHeightItem,
                            fontSize = size.textItem
                        )

                        if (item.desc.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(top = 2.dp),
                                text = item.desc,
                                color = theme.textDesc,
                                lineHeight = size.lineHeightDescAndAlarm,
                                fontSize = size.textDesc
                            )
                        }
                        if (item.changeAlarm) {
                            Text(
                                modifier = Modifier.padding(top = 4.dp),
                                text = alarmText(item),
                                color = theme.textAlarm,
                                fontSize = size.textAlarm,
                                lineHeight = size.lineHeightDescAndAlarm
                            )
                        }
                    }

                    IconButton(
                        onClick = { onClick(item, CHANGE) },
                        modifier = Modifier.padding(end = 4.dp).size(24.dp)
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = if (item.change) theme.chekBoxOn else theme.chekBoxOff,
                            contentDescription = "Check",
                            tint = theme.chekBoxTint
                        )
                    }
                }
            }

            // 3. Правая кнопка (Удаление)
            IconButton(
                onClick = { onClick(item, DELETE) },
                modifier = Modifier.padding(end = 8.dp).size(35.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = theme.iconDelItem,
                    contentDescription = "Удалить",
                    tint = theme.iconDelTint,
                )
            }
        }

        // ВЫЕЗЖАЮЩАЯ ОТДЕЛЬНАЯ КАРТОЧКА ПОДЗАДАЧ (Строго ПОД основной)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
            
             LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            itemsIndexed(
                items = currentSnapshotList,
                key = { _, subItem -> subItem.id } // Ключ работает по ID дела, всё чётко
            ) { index, subItem ->

                ReorderableItem(
                    state = reorderableState,
                    key = subItem.id
                ) { isDragging ->
                    val draggableHandle = Modifier.longPressDraggableHandle(
                        enabled = true,
                        onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                        onDragStopped = {
                            val listWithUpdatedSort = currentSnapshotList.mapIndexed { idx, listItem -> 
                                listItem.copy(sort = idx) 
                            }
                            currentSnapshotList = listWithUpdatedSort
                            onSubDragDropped(listWithUpdatedSort) // Возвращаем чистый List<Item> во ViewModel
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .graphicsLayer { alpha = if (isDragging) 0.5f else 1f }
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
                                               Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // КРУГЛЫЙ ЧЕКБОКС (RadioButton)
                        RadioButton(
                            selected = subItem.change,
                            onClick = {  }, 
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
                        
                        // ИКОНКА УДАЛЕНИЯ ПОДЗАДАЧИ
                        IconButton(
                            onClick = {  }, 
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

                    if (index < listSubItems.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = theme.borderCardMenuItem.copy(alpha = 0.15f)
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







// @Composable
// fun CardItem(
//     item: Item,
//     listSubItems: List<SubItem> = emptyList(),
//     selectedFileUri: String = "",
//     theme: Theme = ThemeNeon(),
//     size: Size = SizeNormal(),
//     dragModifier: Modifier = Modifier,
//     onClick: (Item, Int) -> Unit = { _, _ -> }, // 2 аргумента для главных дел
// ) {
//     var isExpanded by remember { mutableStateOf(false) }

//     Column(modifier = Modifier.fillMaxWidth()) {
//         Row(
//             modifier = Modifier.fillMaxWidth(),
//             verticalAlignment = Alignment.CenterVertically
//         ) {
//             // 1. Левая кнопка (Будильник)
//             Box(
//                 modifier = Modifier
//                     .padding(start = 8.dp)
//                     .size(35.dp)
//                     .combinedClickable(
//                         onClick = { onClick(item, ALARM) },
//                         onLongClick = { onClick(item, ALARM_LONG) }
//                     )
//             ) {
//                 Icon(
//                     imageVector = Icons.Default.Alarm,
//                     contentDescription = "Будильник",
//                     tint = if (item.changeAlarm) theme.tintAlarmOn else theme.tintAlarmOff,
//                     modifier = Modifier.fillMaxSize(),
//                 )
//             }

//             // 2. Центральная ОСНОВНАЯ карточка
//             Card(
//                 modifier = Modifier
//                     .padding(start = 5.dp, end = 5.dp)
//                     .weight(1f)
//                     .clip(RoundedCornerShape(15.dp))
//                     .border(
//                         2.dp,
//                         if (item.change) theme.cardItemBorderTrue 
//                         else if (item.changeAlarm) theme.cardItemBorderAlarm 
//                         else theme.cardItemBorderFalse,
//                         RoundedCornerShape(15.dp)
//                     )
//                     .clickable { onClick(item, CHANGE_ITEM) }
//                     .then(dragModifier),
//                 shape = RoundedCornerShape(15.dp),
//                 colors = CardDefaults.cardColors(
//                     containerColor = if (item.change) theme.cardItemTrue 
//                     else if (item.changeAlarm) theme.cardItemAlarm 
//                     else theme.cardItemFalse
//                 )
//             ) {
//                 Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(start = 6.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
//                     verticalAlignment = Alignment.CenterVertically,
//                 ) {
//                     // Иконка стрелочки-спойлера
//                     if (item.uri.isNotEmpty() || listSubItems.isNotEmpty()) {
//                         IconButton(
//                             onClick = { isExpanded = !isExpanded },
//                             modifier = Modifier.padding(start = 4.dp).size(24.dp)
//                         ) {
//                             Icon(
//                                 modifier = Modifier.fillMaxSize(),
//                                 imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                 contentDescription = "Раскрыть",
//                                 tint = theme.iconTint
//                             )
//                         }
//                     }

//                     Column(
//                         modifier = Modifier
//                             .weight(1f)
//                             .padding(start = 6.dp, end = 6.dp),
//                     ) {
//                         Text(
//                             text = item.name,
//                             color = theme.textColor,
//                             lineHeight = size.lineHeightItem,
//                             fontSize = size.textItem
//                         )

//                         if (item.desc.isNotEmpty()) {
//                             Text(
//                                 modifier = Modifier.padding(top = 2.dp),
//                                 text = item.desc,
//                                 color = theme.textDesc,
//                                 lineHeight = size.lineHeightDescAndAlarm,
//                                 fontSize = size.textDesc
//                             )
//                         }
//                         if (item.changeAlarm) {
//                             Text(
//                                 modifier = Modifier.padding(top = 4.dp),
//                                 text = alarmText(item),
//                                 color = theme.textAlarm,
//                                 fontSize = size.textAlarm,
//                                 lineHeight = size.lineHeightDescAndAlarm
//                             )
//                         }
//                     }

//                     IconButton(
//                         onClick = { onClick(item, CHANGE) },
//                         modifier = Modifier.padding(end = 4.dp).size(24.dp)
//                     ) {
//                         Icon(
//                             modifier = Modifier.fillMaxSize(),
//                             imageVector = if (item.change) theme.chekBoxOn else theme.chekBoxOff,
//                             contentDescription = "Check",
//                             tint = theme.chekBoxTint
//                         )
//                     }
//                 }
//             }

//             // 3. Правая кнопка (Удаление)
//             IconButton(
//                 onClick = { onClick(item, DELETE) },
//                 modifier = Modifier.padding(end = 8.dp).size(35.dp)
//             ) {
//                 Icon(
//                     modifier = Modifier.fillMaxSize(),
//                     imageVector = theme.iconDelItem,
//                     contentDescription = "Удалить",
//                     tint = theme.iconDelTint,
//                 )
//             }
//         }

//         // ВЫЕЗЖАЮЩАЯ ОТДЕЛЬНАЯ КАРТОЧКА ПОДЗАДАЧ (Строго ПОД основной)
//         AnimatedVisibility(
//             visible = isExpanded,
//             enter = expandVertically() + fadeIn(),
//             exit = shrinkVertically() + fadeOut()
//         ) {
//             Card(
//                 modifier = Modifier
//                     .fillMaxWidth()
//                     .padding(start = 48.dp, end = 48.dp, top = 6.dp, bottom = 4.dp) 
//                     .clip(RoundedCornerShape(15.dp))
//                     .border(1.dp, theme.borderCardMenuItem, RoundedCornerShape(15.dp)),
//                 shape = RoundedCornerShape(15.dp),
//                 colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
//             ) {
//                 Column(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(12.dp)
//                 ) {
//                     if (selectedFileUri.isNotEmpty()) {
//                         AsyncImage(
//                             model = selectedFileUri,
//                             contentDescription = "Фото",
//                             modifier = Modifier
//                                 .padding(bottom = 12.dp)
//                                 .size(75.dp)
//                                 .clip(RoundedCornerShape(10.dp))
//                                 .clickable { onClick(item, IMAGE) },
//                             contentScale = ContentScale.Crop,
//                         )
//                     }

//                     Column(modifier = Modifier.fillMaxWidth()) {
//                         listSubItems.forEachIndexed { index, subItem ->
//                            Row(
//                         modifier = Modifier
//                             .fillMaxWidth()
//                             .padding(vertical = 6.dp),
//                         verticalAlignment = Alignment.CenterVertically,
//                     ) {
//                         // КРУГЛЫЙ ЧЕКБОКС (RadioButton)
//                         RadioButton(
//                             selected = subItem.change,
//                             onClick = {  }, 
//                             colors = RadioButtonDefaults.colors(
//                                 selectedColor = theme.chekBoxTint,
//                                 unselectedColor = theme.borderCardMenuItem
//                             ),
//                             modifier = Modifier.size(20.dp)
//                         )

//                         Text(
//                             text = subItem.name,
//                             color = if (subItem.change) theme.textDesc else theme.textColor,
//                             fontSize = size.textDesc,
//                             style = if (subItem.change) {
//                                 val currentStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current
//                                 currentStyle.copy(textDecoration = TextDecoration.LineThrough)
//                             } else {
//                                 LocalTextStyle.current
//                             },
//                             modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp)
//                         )
                        
//                         // ИКОНКА УДАЛЕНИЯ ПОДЗАДАЧИ
//                         IconButton(
//                             onClick = {  }, 
//                             modifier = Modifier.size(24.dp)
//                         ) {
//                             Icon(
//                                 imageVector = Icons.Default.Close,
//                                 contentDescription = "Удалить подзадачу",
//                                 tint = theme.textDesc,
//                                 modifier = Modifier.size(16.dp)
//                             )
//                         }
//                     }

//                     if (index < listSubItems.lastIndex) {
//                         HorizontalDivider(
//                             thickness = 0.5.dp,
//                             color = theme.borderCardMenuItem.copy(alpha = 0.15f)
//                         )
//                     }
//                 }
//             }
//         }
//     }
// }
//     }
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
