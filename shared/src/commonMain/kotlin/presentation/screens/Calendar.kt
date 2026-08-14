package presentation.screens

import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import data.room.model.Item
import data.room.model.ItemWithSubItems
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.ThemeZabor
import kotlin.time.Clock
import kotlin.time.Instant
import data.room.model.SubItem

@Composable
fun Calendar(
    viewModel: MainViewModel,
    onClickSubItem: (SubItem, Int) -> Unit = { _, _ -> },
    onSubDragDropped: (List<SubItem>) -> Unit = {},
    onClick : (Item, Int) -> Unit = {_,_->},
    onAddItem : (Long) -> Unit = {}){


    val listItems by viewModel.getCalendarWithSubItemsCombine.collectAsStateWithLifecycle(emptyList())

        CalendarContent(
        listItems = listItems,
        selectedFileUri = {uri-> viewModel.getUri(uri)},
        theme = viewModel.themeState,
        size = viewModel.sizeState,
        onSubDragDropped = {listSubItems->onSubDragDropped(listSubItems) },
        onClick = {item, action -> onClick(item, action)},
        onClickSubItem = {subItem,action -> onClickSubItem(subItem,action)}
        onAddItem = onAddItem,
        message = {message-> viewModel.sendMessage(message) })
    
    // CalendarContent(
    //     listItems = listItems,
    //     selectedFileUri = {uri-> viewModel.getUri(uri)},
    //     theme = viewModel.themeState,
    //     size = viewModel.sizeState,
    //     onClick = onClick,
    //     onAddItem = onAddItem,
    //     message = {message-> viewModel.sendMessage(message) })

}


@Composable
fun CalendarContent(
    listItems: List<ItemWithSubItems> = emptyList(),
    selectedFileUri: (String) -> String = { _ -> "" },
    theme: Theme = ThemeNeon(),
    size : Size = SizeNormal(),
    onClick : (Item, Int) -> Unit = {_,_->},
    onClickSubItem: (SubItem, Int) -> Unit = { _, _ -> },
    onSubDragDropped: (List<SubItem>) -> Unit = {},
    onAddItem : (Long) -> Unit = {},
    message : (String) -> Unit = {} ) {

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }

    val daysOfWeek = remember { daysOfWeek() }
    var selectedDate by remember { mutableStateOf(today) }
    val coroutineScope = rememberCoroutineScope()

    val tasksByDate = remember(listItems) {
        listItems.groupBy { item ->
            Instant.fromEpochMilliseconds(item.item.alarmTime)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date 
        }
    }

    // 3. Фильтруем задачи только для ВЫБРАННОЙ даты для списка ВНИЗУ
    val selectedDateTasks = remember(listItems, selectedDate) {
        listItems.filter { item ->
            val itemDate = Instant.fromEpochMilliseconds(item.item.alarmTime)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            itemDate == selectedDate
        }
    }


    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(
        modifier = Modifier.fillMaxWidth()
            ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            item {
                    HorizontalCalendar(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        dayContent = { day ->
                            val hasTasks = tasksByDate.containsKey(day.date)
                            Day(day,
                                isSelected = selectedDate == day.date,
                                theme = theme,
                                hasTasks = hasTasks,
                                isToday = day.date == today) { day ->
                                selectedDate = day.date
                            }} ,
                        monthHeader = { month ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                // Панель управления: Стрелка влево -> Месяц по центру -> Стрелка вправо
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                    ,

                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Кнопка назад
                                    IconButton(
                                        onClick = {
                                            // Используем coroutineScope, чтобы прокрутить календарь назад
                                            coroutineScope.launch {
                                                state.animateScrollToMonth(month.yearMonth.minusMonths(1))
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, // или ваша иконка стрелки
                                            contentDescription = "Предыдущий месяц",
                                            tint = theme.tintAlarmOff
                                        )
                                    }

                                    // Название месяца СТРОГО ПО ЦЕНТРУ
                                    Text(
                                        text = month.yearMonth.month.displayText(),
                                        color = theme.textColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Кнопка вперед
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                state.animateScrollToMonth(month.yearMonth.plusMonths(1))
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = "Следующий месяц",
                                            tint = theme.tintAlarmOff
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Дни недели (Пн, Вт, Ср...), которые теперь будут четко над цифрами
                                MonthHeader(daysOfWeek = daysOfWeek, theme = theme)
                            }
                        }
                    )

                }
            if (selectedDateTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp), // Задаем красивый фиксированный отступ от календаря вниз
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Список дел пуст",
                            fontSize = 34.sp,
                            color = theme.textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else items(selectedDateTasks) { item ->

                
                    CardItem(
                        item = item.item,
                        listSubItems = item.subItems,
                        selectedFileUri = selectedFileUri(item.item.uri),
                        theme = theme, 
                        size = size,
                        onSubDragDropped = {listSubItems->onSubDragDropped(listSubItems) },
                        onClick = {item, action -> onClick(item, action)},
                        onClickSubItem = {subItem,action -> onClickSubItem(subItem,action)}
                    )


                    // CardItem(
                    //     item = item.item,
                    //     listSubItems = item.subItems,
                    //     selectedFileUri = selectedFileUri(item.item.uri),
                    //     theme = theme, 
                    //     size = size,
                    //     onSubDragDropped = {listSubItems->onSubDragDropped(listSubItems) },
                    //     onClick = onClick,
                    //     onClickSubItem = onClickSubItem
                    // )
                         
            }




        }
        Box(modifier = Modifier.fillMaxWidth()
            .padding(8.dp)

        ){

            IconButton(modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
                onClick = {
    val systemTimeZone = TimeZone.currentSystemDefault()
    // Получаем актуальное "сегодня" прямо в момент клика
    val realToday = Clock.System.now().toLocalDateTime(systemTimeZone).date

    if (selectedDate < realToday) {
        message("Вы выбрали дату которая уже прошла")
    } else {
        // Конвертируем в миллисекунды только если дата валидна
        val selectedDayMillis = selectedDate
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()
        onAddItem(selectedDayMillis)
    }
},
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

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean, // Флаг текущего дня
    hasTasks: Boolean,
    theme: Theme = ThemeNeon(),
    onClick: (CalendarDay) -> Unit
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(
                enabled = true,
                onClick = { onClick(day) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
    modifier = Modifier
        .size(30.dp)
        // 1. Сначала применяем обводку по условию
        .then(
             if (isSelected) Modifier.background(color = theme.textColor, shape = CircleShape)
             else if (isToday)  Modifier.border(width = 2.dp, color = theme.textColor, shape = CircleShape)
             else Modifier
        )
        // 2. Только потом обрезаем контент изнутри (если необходимо)
        .clip(CircleShape),
    contentAlignment = Alignment.Center
) {
            Text(
                text = day.date.dayOfMonth.toString(),
                // Если день сегодняшний, можно сделать текст белым, даже если он не выбран
                color = when {
                    isSelected -> theme.colorCalendarDaySelect
                    isToday -> theme.textColor
                    isCurrentMonth -> theme.textColor
                    else -> Color.Transparent
                },
                fontSize = 16.sp,
                fontWeight = if (isToday) FontWeight.Medium else FontWeight.Normal,
                lineHeight = 16.sp
            )
        }

        // Ваша иконка дела под кружком
        if (hasTasks && isCurrentMonth) {
            Icon(
                imageVector =  Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(8.dp),
                tint = Color.Red
            )
        } else {
            Spacer(modifier = Modifier.size(8.dp).padding(top = 2.dp))
        }
    }
}


@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>, theme: Theme) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f), // Занимает ровно 1/7 ширины экрана
                textAlign = TextAlign.Center,    // Текст строго по центру своей колонки
                text = dayOfWeek.displayText(),
                color = theme.textColor, // Сделаем чуть блеклыми, чтобы не сливались с числами
                fontSize = 14.sp
            )
        }
    }
}

fun DayOfWeek.displayText(): String {
    return when (this) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
    }
}

// Расширение для месяцев на русском
fun Month.displayText(): String {
    return when (this) {
        Month.JANUARY -> "Январь"
        Month.FEBRUARY -> "Февраль"
        Month.MARCH -> "Март"
        Month.APRIL -> "Апрель"
        Month.MAY -> "Май"
        Month.JUNE -> "Июнь"
        Month.JULY -> "Июль"
        Month.AUGUST -> "Август"
        Month.SEPTEMBER -> "Сентябрь"
        Month.OCTOBER -> "Октябрь"
        Month.NOVEMBER -> "Ноябрь"
        Month.DECEMBER -> "Декабрь"
    }
}

@Preview
@Composable
fun PrevCalendar(){
    CalendarContent(theme = ThemeZabor())
}
