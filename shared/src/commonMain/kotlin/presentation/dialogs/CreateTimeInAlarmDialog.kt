package presentation.dialogs



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDateInAlarmDialog(viewModel: MainViewModel){
 val item = viewModel.showDialog.item ?: return
 val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.showDialog = DialogState() },
            confirmButton = {
                TextButton(onClick = {
                  val date = datePickerState.selectedDateMillis ?: 0L
                  val currentDate = getTodayMidnightInMillis()
                 if(date < currentDate) {
                  viewModel.sendMessage("Вы выбрали время которое прошло") 
                  viewModel.showDialog = DialogState()
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
            DatePicker(state = datePickerState)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeInAlarmDialog(date: Long,viewModel: MainViewModel){
  val item = viewModel.showDialog.item ?: return
  val timePickerState = rememberTimePickerState()
  
 AlertDialog(
            onDismissRequest = { viewModel.showDialog = DialogState() },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    
                    val currentTime: Long = Clock.System.now().toEpochMilliseconds()
                    val timeAlarm = convertTime(date,hour,minute)
                    
                    if(timeAlarm < currentTime){
                     viewModel.sendMessage("Вы выбрали время которое прошло") 
                      viewModel.showDialog = DialogState()  
                    }
                    else{
                      viewModel.showDialog = DialogState(ACTION,item.copy(alarmTime = timeAlarm)) 
                    }
                    
                    
                   
                }) {
                    Text("Готово")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDialog = DialogState(NOTIFICATION, item)  }) {
                    Text("Назад")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
}

@Composable
fun CreateActionInAlarmDialog(viewModel: MainViewModel){
   val item = viewModel.showDialog.item ?: return
   val listAction = listOf("Один раз", "Каждый день", "Каждую неделю","Каждый месяц", "Каждый год")
   var selected by remember { mutableStateOf(listAction[0]) }
   val isPremium = viewModel.getPremium()
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
                 viewModel.insertAlarm(item.copy(interval = selectedInterval))
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
    Column(){
      Text("Как часто повторять?")
      Text(text = selected, fontSize = 28.sp, modifier = Modifier.padding(10.dp))
                Column(Modifier.selectableGroup()) {
                    listAction.forEachIndexed {index, text ->
                      val isOptionEnabled = index == 0 || isPremium
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
                                onClick = { selected = text }
                            )
                            Text( text = text, fontSize = 24.sp )
                        }
                    }

                    if (!isPremium) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "* Повторяющиеся будильники доступны только в Премиум-версии",
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

private fun convertTime(date: Long, hour: Int, minuts: Int): Long {
    // 1. Извлекаем чистую дату (год, месяц, день) из UTC-миллисекунд календаря
    val instantFromCalendar = Instant.fromEpochMilliseconds(date)
    val localDate = instantFromCalendar.toLocalDateTime(TimeZone.UTC).date

    // 2. Создаем время на основе выбранных часов и минут
    val localTime = LocalTime(hour, minuts, 0, 0)

    // 3. Объединяем их в единый LocalDateTime
    val localDateTime = LocalDateTime(localDate, localTime)

    // 4. Конвертируем обратно в Unix Timestamp с учетом часового пояса устройства
    val systemTimeZone = TimeZone.currentSystemDefault()
    return localDateTime.toInstant(systemTimeZone).toEpochMilliseconds()
}

private fun getTodayMidnightInMillis(): Long {
    // 1. Получаем текущий часовой пояс устройства
    val currentZone = TimeZone.currentSystemDefault()
    
    // 2. Получаем текущий момент времени
    val now = Clock.System.now()
    
    // 3. Переводим текущее время в локальную дату (год-месяц-день)
    val todayDate = now.toLocalDateTime(currentZone).date
    
    // 4. Получаем начало этого дня (00:00) в миллисекундах UTC
    return todayDate.atStartOfDayIn(currentZone).toEpochMilliseconds()
}

  
