package presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

import CommonConst.TIME
import CommonConst.ACTION
import CommonConst.NOTIFICATION
import CommonConst.ALARM_ONE
import CommonConst.ALARM_DAY
import CommonConst.ALARM_WEEK
import CommonConst.ALARM_MONTH
import CommonConst.ALARM_YEAR

import MainViewModel
import androidx.compose.foundation.layout.Arrangement
import kotlin.time.Duration.Companion.hours
import androidx.compose.runtime.collectAsState

import kotlinx.datetime.Clock
import kotlinx.datetime.atStartOfDayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDateInAlarmDialog(viewModel: MainViewModel){

    
    val kotlinInstant = kotlin.time.Clock.System.now()
    val nowInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(kotlinInstant.toEpochMilliseconds())
    
    val todayDate = nowInstant
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .date
    val todayMillis = kotlinx.datetime.LocalDateTime(
        year = todayDate.year,
        monthNumber = todayDate.monthNumber,
        dayOfMonth = todayDate.dayOfMonth,
        hour = 0, minute = 0, second = 0, nanosecond = 0
    ).toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
    

 val item = viewModel.showDialog.item ?: return
 val datePickerState = rememberDatePickerState((if (item.alarmTime == 0L) todayMillis else item.alarmTime ))
  var errorMessage by remember {mutableStateOf<String?>(null)} 
  LaunchedEffect(datePickerState.selectedDateMillis) {
    errorMessage = null // Сбрасываем ошибку, если пользователь выбрал другую дату
}
        DatePickerDialog(
            onDismissRequest = { viewModel.showDialog = DialogState() },
            confirmButton = {
                TextButton(onClick = {

                    val date = datePickerState.selectedDateMillis ?: 0L

                    val selectedDate = kotlinx.datetime.Instant
                    .fromEpochMilliseconds(date)
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    .date

                    // Получаем текущую локальную дату без зачеркнутых классов



                 if(selectedDate < todayDate) {
                  errorMessage = "Вы выбрали дату которая прошла" 
                 }
                 else viewModel.showDialog = DialogState(TIME,item.copy(alarmTime = date))
                }) {
                    Text("Далее")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDialog = DialogState()}) {
                    Text("Отмена")
                }
            }
        ) {
          DatePicker(state = datePickerState,
              title = {
                  if (errorMessage != null) {
                      Text(
                          text = errorMessage!!,
                          color = MaterialTheme.colorScheme.error,
                          style = MaterialTheme.typography.bodyMedium,
                          modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                      )
                  } else {
                      // Стандартный заголовок, если ошибки нет
                      Text(
                          text = "Выберите дату",
                          modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                      )
                  }


            
        })
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeInAlarmDialog(date: Long,viewModel: MainViewModel){
  val item = viewModel.showDialog.item ?: return
    val currentTime: Long = kotlin.time.Clock.System.now().toEpochMilliseconds()

    val instant = Instant.fromEpochMilliseconds(currentTime)

// 2. Получаем локальное время для часового пояса устройства
    val systemTZ = TimeZone.currentSystemDefault()
    val currentDateTime = instant.toLocalDateTime(systemTZ)

// 3. Достаем час и минуту в формате Int
    val currentHour: Int = currentDateTime.hour
    val currentMinute: Int = currentDateTime.minute

    currentTime.hours
    val timePickerState = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = true // Это принудительно включает 24-часовой формат и убирает кнопки AM/PM
    )
  var errorMessage by remember {mutableStateOf<String?>(null)} 
  LaunchedEffect(timePickerState.hour, timePickerState.minute) {
    errorMessage = null // Сбрасываем ошибку, если пользователь выбрал другую дату
}
 AlertDialog(
            onDismissRequest = { viewModel.showDialog = DialogState() },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    

                    val timeAlarm = convertTime(date,hour,minute)
                    
                    if(timeAlarm < currentTime){
                     errorMessage = "Вы выбрали время которое прошло"   
                    }
                    else{
                      viewModel.showDialog = DialogState(ACTION,item.copy(alarmTime = timeAlarm)) 
                    }
                    
                    
                   
                }) {
                    Text("Готово")
                }
            },
            dismissButton = {
    TextButton(onClick = {
            viewModel.showDialog = DialogState(CommonConst.NOTIFICATION, item)
    }) {
        Text("Назад")
    }
},
            text = {
             Column(){
              TimePicker(state = timePickerState )
              errorMessage?.let { errorText ->
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
             }
                
            }
        )
}


@Composable
fun CreateActionInAlarmDialog(viewModel: MainViewModel){
   val item = viewModel.showDialog.item ?: return
   val listAction = listOf("Один раз", "Каждый день", "Каждую неделю","Каждый месяц", "Каждый год")
   var selected by remember { mutableStateOf(listAction[0]) }
   val isPremium = viewModel.premiumState.collectAsState()
  AlertDialog(
    onDismissRequest = { viewModel.showDialog = DialogState() },
    confirmButton = {
                TextButton(onClick = {
                  val selectedInterval = when(selected){
                    "Один раз"-> ALARM_ONE
                    "Каждый день" -> ALARM_DAY
                    "Каждую неделю" -> ALARM_WEEK
                    "Каждый месяц"-> ALARM_MONTH
                    "Каждый год" -> ALARM_YEAR
                     else -> ALARM_ONE
                  }
                    val newitem = item.copy(interval = selectedInterval, change = false, changeAlarm = true)
                    viewModel.updateItem(newitem)
                    viewModel.insertAlarm(newitem)
                 viewModel.showDialog = DialogState()
                }) {
                    Text("Ок")
                }
            },
    dismissButton = {
                TextButton(onClick = { viewModel.showDialog = DialogState(TIME, item)  }) {
                    Text("Назад")
                }
            },

    text = {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
      Text("Как часто повторять?")
                Column(Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(10.dp) ) {
                    listAction.forEachIndexed {index, text ->
                      val isOptionEnabled = index == 0 || isPremium.value
                        Row( Modifier.fillMaxWidth()
                            .selectable(
                                selected = (text == selected),
                                enabled = isOptionEnabled,
                                onClick = { selected = text }),
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            RadioButton(
                                selected = (text == selected),
                                enabled = isOptionEnabled,
                                onClick = null 
                            )
                            Text( text = text, fontSize = 24.sp )
                        }
                    }

                    if (!isPremium.value) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "* Повторяющиеся будильники доступны в PREMIUM версии",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error, // Или заведите свой цвет премиума (например, золотой)
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                }
                
    }
      
    }
)
}
private fun convertTime(date: Long, hour: Int, minutes: Int): Long {
    // Получаем текущий часовой пояс устройства пользователя
    val systemTimeZone = TimeZone.currentSystemDefault()

    // 1. ✅ ИСПРАВЛЕНО: Извлекаем чистую дату с учетом часового пояса ТЕЛЕФОНА
    val instantFromCalendar = Instant.fromEpochMilliseconds(date)
    val localDate = instantFromCalendar.toLocalDateTime(systemTimeZone).date

    // 2. Создаем время на основе выбранных пользователем часов и минут
    val localTime = LocalTime(hour, minutes, 0, 0)

    // 3. Объединяем их в единый LocalDateTime
    val localDateTime = LocalDateTime(localDate, localTime)

    // 4. Конвертируем обратно в Unix Timestamp по местному времени устройства
    return localDateTime.toInstant(systemTimeZone).toEpochMilliseconds()
}



  
