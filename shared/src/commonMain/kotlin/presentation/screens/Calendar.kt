package presentation.screens

import CommonConst.ALARM
import CommonConst.ALARM_LONG
import CommonConst.CHANGE
import CommonConst.CHANGE_ITEM
import CommonConst.DELETE
import CommonConst.IMAGE
import MainViewModel
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.LineHeightStyle
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
import data.room.Item
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


@Composable
fun Calendar(
    viewModel: MainViewModel,
    onClick : (Item, Int) -> Unit = {_,_->},
    onAddItem : (Long) -> Unit = {}){


    val listItems by viewModel.getItemCalendarCombine.collectAsStateWithLifecycle(emptyList())
    CalendarContent(
        listItems = listItems,
        theme = viewModel.themeState,
        size = viewModel.sizeState,
        onClick = onClick,
        onAddItem = onAddItem,)

}


@Composable
fun CalendarContent(
    listItems: List<Item> = emptyList(),
    theme: Theme = ThemeNeon(),
    size : Size = SizeNormal(),
    onClick : (Item, Int) -> Unit = {_,_->},
    onAddItem : (Long) -> Unit = {}) {

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }

    val daysOfWeek = remember { daysOfWeek() }
    var selectedDate by remember { mutableStateOf(today) }
    val coroutineScope = rememberCoroutineScope()

    val tasksByDate = remember(listItems) {
        listItems.groupBy { item ->
            Instant.fromEpochMilliseconds(item.alarmTime)
                .toLocalDateTime(TimeZone.UTC).date // ✅ Теперь 100% совместимо
        }
    }

    // 3. Фильтруем задачи только для ВЫБРАННОЙ даты для списка ВНИЗУ
    val selectedDateTasks = remember(listItems, selectedDate) {
        listItems.filter { item ->
            val itemDate = Instant.fromEpochMilliseconds(item.alarmTime)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // Все три элемента будут идеально ровно по центру высоты
                ) {
                    // 1. Левая кнопка/текст

                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(35.dp).combinedClickable(
                                onClick = {onClick(item,ALARM)},
                                onLongClick = {onClick(item,ALARM_LONG)}
                            )



                    ) {

                        Icon(
                            imageVector = Icons.Default.Alarm, // Нужен импорт androidx.compose.material.icons.Icons
                            contentDescription = "Будильник",
                            tint =  if(item.changeAlarm) theme.tintAlarmOn else theme.tintAlarmOff,
                            modifier = Modifier.fillMaxSize(),
                        )

                    }


                    // 2. Центральная карточка (занимает всё оставшееся пространство)

                    Card(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                2.dp,
                                if(item.change) theme.cardItemBorderTrue
                                else if (item.changeAlarm) theme.cardItemBorderAlarm
                                else theme.cardItemBorderFalse,
                                RoundedCornerShape(15.dp)

                            ).clickable{onClick(item,CHANGE_ITEM)}
                        ,

                        shape = RoundedCornerShape(16.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                if(item.change) theme.cardItemTrue
                                else if(item.changeAlarm) theme.cardItemAlarm
                                else theme.cardItemFalse
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, top = 8.dp, bottom = 8.dp,end = 6.dp)
                            ,
                            verticalAlignment = Alignment.CenterVertically,


                            ) {
                            if (item.uri != "") IconButton(
                                onClick = { onClick(item,IMAGE) },
                                modifier = Modifier.padding(start = 8.dp).size(24.dp)

                            ) {

                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = theme.iconImage, // Нужен импорт androidx.compose.material.icons.Icons
                                    contentDescription = "Картинка",
                                    tint = theme.iconTint
                                )

                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 6.dp, end = 6.dp),
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 5.dp,end = 5.dp),
                                    text = item.name,
                                    color = theme.textColor,
                                    lineHeight = size.lineHeightItem,
                                    fontSize = size.textItem)
                                if (item.desc.isNotEmpty())  {
                                    Text(
                                        modifier = Modifier.padding(start = 5.dp,top = 2.dp, end = 5.dp),
                                        text = item.desc,
                                        color = theme.textDesc,
                                        lineHeight = size.lineHeightDescAndAlarm,
                                        fontSize = size.textDesc) }
                                if (item.changeAlarm) {
                                    Text(
                                        modifier = Modifier.padding(start = 5.dp,top = 4.dp, end = 5.dp),
                                        text = alarmText(item),
                                        color = theme.textAlarm,
                                        fontSize = size.textAlarm,
                                        lineHeight = size.lineHeightDescAndAlarm
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onClick(item,CHANGE) },
                                modifier = Modifier.padding(end = 8.dp).size(24.dp)
                            ) {

                                Icon(
                                    modifier = Modifier.fillMaxSize(),

                                    imageVector =
                                        if(item.change)  theme.chekBoxOn
                                        else theme.chekBoxOff,
                                    contentDescription = "Chek",
                                    tint = theme.chekBoxTint
                                )

                            }
                        }
                    }



                    // 3. Правая кнопка/текст



                    IconButton(

                        onClick = {onClick(item,DELETE)  },
                        modifier = Modifier.padding(end = 8.dp).size(35.dp)


                    ) {

                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = theme.iconDelItem, // Нужен импорт androidx.compose.material.icons.Icons
                            contentDescription = "Меню",
                            tint = theme.iconDelTint,

                            )

                    }



                } // Конец Row
            }




        }
        Box(modifier = Modifier.fillMaxWidth()
            .padding(8.dp)

        ){

            IconButton(modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
                onClick = {
                    val selectedDayMillis = selectedDate
                        // Фиксируем начало дня строго по UTC (00:00), игнорируя пояс телефона
                        .atStartOfDayIn(TimeZone.UTC)
                        .toEpochMilliseconds()
                    onAddItem(selectedDayMillis) },
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
            if (isSelected) {
                Modifier.border(width = 2.dp, color = theme.textColor, shape = CircleShape)
            } else {
                Modifier
            }
        )
        // 2. Только потом обрезаем контент изнутри (если необходимо)
        .clip(CircleShape),
    contentAlignment = Alignment.Center
) {
            Text(
                text = day.date.dayOfMonth.toString(),
                // Если день сегодняшний, можно сделать текст белым, даже если он не выбран
                color = when {
                    isSelected -> theme.textColor
                    isToday -> Color.Red
                    isCurrentMonth -> theme.textColor
                    else -> theme.textDesc
                },
                fontSize = 16.sp,
                fontWeight = if (isToday) FontWeight.Medium else FontWeight.Normal,
                lineHeight = 16.sp
            )
        }

        // Ваша иконка дела под кружком
        if (hasTasks && isCurrentMonth) {
            Icon(
                imageVector =  Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(8.dp),
                tint = if (isSelected) Color.White else Color.Yellow
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
