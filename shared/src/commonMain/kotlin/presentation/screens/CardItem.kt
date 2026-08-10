package presentation.screens


// ВОССТАНОВИЛИ: Полный набор библиотек времени для твоих методов расчета дат внизу файла

// ВОССТАНОВИЛИ: Все до единой константы алармов, которые ругались в логе компиляции

// Данные и темы проекта


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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.room.model.Item
import data.room.model.SubItem
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon
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
    onClick: (Item, Int) -> Unit = { _, _ -> },
    onClickSubItem: (SubItem, Int) -> Unit = { _, _ -> },
) {
    var isExpanded by remember { mutableStateOf(false) }
    var currentSnapshotList by remember { mutableStateOf(listSubItems) }
    val cardShape by remember(isExpanded) {
        derivedStateOf {
            if (isExpanded) {
                RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
            } else {
                RoundedCornerShape(15.dp)
            }
        }
    }

    val cardMiddleShape = if (isExpanded) {
        when {
            // 1. Есть и фото, и подзадачи -> зажата между ними, все углы прямые (0.dp)
            selectedFileUri.isNotEmpty() && currentSnapshotList.isNotEmpty() ->
                RoundedCornerShape(0.dp)

            // 2. Есть только фото (подзадач нет) -> средняя карта стала НИЖНЕЙ, скругляем НИЗ
            selectedFileUri.isNotEmpty() ->
                RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 15.dp, bottomEnd = 15.dp)

            // 3. Фото нет, но есть подзадачи -> средняя карта стала ВЕРХНЕЙ, скругляем ВЕРХ
            currentSnapshotList.isNotEmpty() ->
                RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 0.dp, bottomEnd = 0.dp)

            // 4. ОШИБОЧНЫЙ ХВОСТ: Фото нет И подзадач нет, но кнопка раскрытия нажата (isExpanded == true)
            // В этом случае средняя карточка осталась совершенно одна! Она должна скруглить ВСЕ углы (15.dp)
            else ->
                RoundedCornerShape(15.dp)
        }
    } else {
        RoundedCornerShape(15.dp)
    }

    val menuCardShape by remember(isExpanded) {
        derivedStateOf {
            if (isExpanded) {
                RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 15.dp, bottomEnd = 15.dp)
            } else {
                RoundedCornerShape(15.dp)
            }
        }
    }

    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current




    LaunchedEffect(listSubItems) {
        if (currentSnapshotList != listSubItems) {
            currentSnapshotList = listSubItems
        }
    }

    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            val fromIdx = from.index
            val toIdx = to.index
            if (fromIdx in currentSnapshotList.indices && toIdx in currentSnapshotList.indices) {
                val updatedList =
                    currentSnapshotList.toMutableList().apply { add(toIdx, removeAt(fromIdx)) }
                currentSnapshotList = updatedList
            }
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (selectedFileUri.isNotEmpty() && isExpanded) {
                Card(
                    modifier = Modifier
                        .padding(start = 48.dp, end = 48.dp)
                        .weight(1f)
                        .clip(cardShape)
                        .then(
                            if (isExpanded) {
                                // Если раскрыта: обводка по трем сторонам без низа
                                Modifier.borderThreeSidesRounded(
                                    strokeWidth = 2.dp,
                                    color = if (item.change) theme.cardItemBorderTrue else if (item.changeAlarm) theme.cardItemBorderAlarm else theme.cardItemBorderFalse,
                                    cornerRadius = 15.dp,
                                    openSide = "BOTTOM_OPEN"
                                )
                            } else {
                                Modifier.border(
                                    2.dp,
                                    if (item.change) theme.cardItemBorderTrue else if (item.changeAlarm) theme.cardItemBorderAlarm else theme.cardItemBorderFalse,
                                    RoundedCornerShape(15.dp)
                                )
                            }
                        )
                        .clickable { onClick(item, CHANGE_ITEM) }
                        .then(dragModifier),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.change) theme.cardItemTrue else if (item.changeAlarm) theme.cardItemAlarm else theme.cardItemFalse
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(75.dp)
                            // Скругляем только верхние углы контейнера
                            .clip(
                                RoundedCornerShape(
                                    topStart = 15.dp,
                                    topEnd = 15.dp
                                )
                            )
                            .clickable { onClick(item, IMAGE) }
                    ) {
                        // 1. Задний размытый фон
                        AsyncImage(
                            model = selectedFileUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(radius = 15.dp), // Эффект размытия
                            contentScale = ContentScale.Crop // Заполняет весь контейнер
                        )

                        // 2. Передний план (оригинальное фото без искажений)
                        AsyncImage(
                            model = selectedFileUri,
                            contentDescription = "Фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit // Картинка помещается целиком без обрезки
                        )
                    }
                }

            }
        }
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

            Card(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .weight(1f)
                    .clip(cardMiddleShape)
                    .then(
                        // Проверяем: раскрыта ЛИ карточка И есть ли ХОТЯ БЫ один смежный блок (фото или подзадача)
                        if (isExpanded && (selectedFileUri.isNotEmpty() || currentSnapshotList.isNotEmpty())) {
                            // Если есть смежные блоки — рисуем кастомный трехсторонний бордер
                            Modifier.borderThreeSidesRounded(
                                strokeWidth = 2.dp,
                                color = if (item.change) theme.cardItemBorderTrue else if (item.changeAlarm) theme.cardItemBorderAlarm else theme.cardItemBorderFalse,
                                cornerRadius = 15.dp,
                                openSide = when {
                                    selectedFileUri.isNotEmpty() && currentSnapshotList.isNotEmpty() -> "LEFT_RIGHT_ONLY"
                                    selectedFileUri.isNotEmpty() -> "TOP_OPEN"
                                    else -> "BOTTOM_OPEN"
                                }
                            )
                        } else {
                            // Если карточка закрыта ИЛИ она раскрыта, но оказалась абсолютно пустой (нет ни фото, ни подзадач) —
                            // рисуем обычный стандартный закрытый бордер по всему периметру!
                            Modifier.border(
                                width = 2.dp,
                                color = if (item.change) theme.cardItemBorderTrue else if (item.changeAlarm) theme.cardItemBorderAlarm else theme.cardItemBorderFalse,
                                shape = cardMiddleShape // Передаем сюда наш правильный шейп (он уже равен RoundedCornerShape(15.dp))
                            )
                        }
                    )
                    .clickable { onClick(item, CHANGE_ITEM) }
                    .then(dragModifier),
                shape = cardMiddleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (item.change) theme.cardItemTrue else if (item.changeAlarm) theme.cardItemAlarm else theme.cardItemFalse
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            )
            {
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

                    Checkbox(
                        checked = item.change,
                        onCheckedChange = { onClick(item, CHANGE) },
                        modifier = Modifier.padding(end = 4.dp).size(24.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = theme.chekBoxTint,
                            uncheckedColor = theme.chekBoxTint
                        )
                    )
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
            visible = isExpanded && currentSnapshotList.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 48.dp, bottom = 4.dp)
                    .clip(menuCardShape)
                    .borderThreeSidesRounded(
                        strokeWidth = 2.dp,
                        color = if (item.change) theme.cardItemBorderTrue else if (item.changeAlarm) theme.cardItemBorderAlarm else theme.cardItemBorderFalse,
                        cornerRadius = 15.dp,
                        openSide = "TOP_OPEN"
                    ),
                shape = menuCardShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (item.change) theme.cardItemTrue else if (item.changeAlarm) theme.cardItemAlarm else theme.cardItemFalse
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            )

            {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                ) {
                    if (listSubItems.isNotEmpty()) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = theme.textColor.copy(alpha = 0.15f),
                            modifier = Modifier.padding(bottom = 6.dp) // Отталкиваем текст от верхней линии
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .longPressDraggableHandle(
                                            enabled = true,
                                            onDragStarted = {
                                                haptic.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                            },
                                            onDragStopped = {
                                                val listWithUpdatedSort =
                                                    currentSnapshotList.mapIndexed { idx, listItem ->
                                                        listItem.copy(sort = idx)
                                                    }
                                                currentSnapshotList = listWithUpdatedSort
                                                onSubDragDropped(listWithUpdatedSort)
                                            }
                                        )
                                        .animateItem()
                                        .graphicsLayer { alpha = if (isDragging) 0.5f else 1f }
                                ) {
                                    if (subItem.id != currentSnapshotList.firstOrNull()?.id) {
                                        HorizontalDivider(
                                            thickness = 0.5.dp,
                                            color = theme.borderCardMenuItem.copy(alpha = 0.15f),
                                            modifier = Modifier.padding(bottom = 6.dp) // Отталкиваем текст от верхней линии
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {

                                        Checkbox(
                                            checked = subItem.change,
                                            onCheckedChange = {
                                                onClickSubItem(
                                                    subItem,
                                                    CHANGE_ITEM
                                                )
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = theme.chekBoxTint,
                                                uncheckedColor = theme.borderCardMenuItem,
                                            ),
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Text(
                                            text = subItem.name,
                                            color = if (subItem.change) theme.textDesc else theme.textColor,
                                            fontSize = size.textDesc,
                                            style = if (subItem.change) {
                                                val currentStyle: androidx.compose.ui.text.TextStyle =
                                                    LocalTextStyle.current
                                                currentStyle.copy(textDecoration = TextDecoration.LineThrough)
                                            } else {
                                                LocalTextStyle.current
                                            },
                                            modifier = Modifier.weight(1f)
                                                .padding(start = 8.dp, end = 8.dp)
                                        )

                                        // ИКОНКА УДАЛЕНИЯ ПОДЗАДАЧИ
                                        IconButton(
                                            onClick = {onClickSubItem(subItem, DELETE) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Удалить подзадачу",
                                                tint = theme.textDesc,
                                                modifier = Modifier.fillMaxSize()
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
}


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
    val today: LocalDate =
        kotlinx.datetime.Instant.fromEpochMilliseconds(currentMillis).toLocalDateTime(tz).date

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

fun Modifier.borderThreeSidesRounded(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
    openSide: String
): Modifier = this.drawWithContent {
    drawContent()

    val strokePx = strokeWidth.toPx()
    val radiusPx = cornerRadius.toPx()
    val width = size.width
    val height = size.height
    val halfStroke = strokePx / 2f
    val diameter = radiusPx * 2

    val path = Path().apply {
        when (openSide) {
            "LEFT_RIGHT_ONLY" -> {
                // 1. Левая вертикальная линия (сверху вниз)
                moveTo(halfStroke, 0f)
                lineTo(halfStroke, height)

                // 2. Перемещаем "перо" на правую сторону, не рисуя линию между ними
                moveTo(width - halfStroke, 0f)
                // 3. Правая вертикальная линия (сверху вниз)
                lineTo(width - halfStroke, height)
            }

            "BOTTOM_OPEN" -> {
                moveTo(halfStroke, height)
                lineTo(halfStroke, radiusPx + halfStroke)
                addArc(
                    // ИСПРАВЛЕНО: 'oval' вместо 'rect'
                    oval = Rect(
                        halfStroke,
                        halfStroke,
                        diameter + halfStroke,
                        diameter + halfStroke
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f
                )
                lineTo(width - radiusPx - halfStroke, halfStroke)
                addArc(
                    // ИСПРАВЛЕНО: 'oval' вместо 'rect'
                    oval = Rect(
                        width - diameter - halfStroke,
                        halfStroke,
                        width - halfStroke,
                        diameter + halfStroke
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 90f
                )
                lineTo(width - halfStroke, height)
            }

            "TOP_OPEN" -> {
                moveTo(halfStroke, 0f)
                lineTo(halfStroke, height - radiusPx - halfStroke)
                addArc(
                    // ИСПРАВЛЕНО: 'oval' вместо 'rect'
                    oval = Rect(
                        halfStroke,
                        height - diameter - halfStroke,
                        diameter + halfStroke,
                        height - halfStroke
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -90f
                )
                lineTo(width - radiusPx - halfStroke, height - halfStroke)
                addArc(
                    // ИСПРАВЛЕНО: 'oval' вместо 'rect'
                    oval = Rect(
                        width - diameter - halfStroke,
                        height - diameter - halfStroke,
                        width - halfStroke,
                        height - halfStroke
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -90f
                )
                lineTo(width - halfStroke, 0f)
            }
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokePx,
            cap = StrokeCap.Butt
        )
    )
}

