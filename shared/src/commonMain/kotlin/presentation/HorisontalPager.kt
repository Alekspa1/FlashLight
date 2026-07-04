package presentation

import CommonConst.ALARM
import CommonConst.CHANGE
import CommonConst.CHANGE_ITEM
import CommonConst.DELETE
import CommonConst.DELETE_DIALOG
import CommonConst.IMAGE
import CommonConst.INSERT_DIALOG
import CommonConst.NOTIFICATION
import MainViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

import presentation.dialogs.AddOrChangeItemDialog
import presentation.dialogs.DeleteDialog
import presentation.dialogs.DialogState
import presentation.dialogs.CreateDateInAlarmDialog
import presentation.dialogs.CreateTimeInAlarmDialog
import presentation.dialogs.CreateActionInAlarmDialog

import presentation.screens.ListToDo
import presentation.screens.Notebook

import CommonConst.TIME
import CommonConst.ACTION
import CommonConst.ALARM_LONG
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


import kotlin.time.Clock

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

@Composable
fun MainWeatherPager(paddingValues: PaddingValues = PaddingValues(),
                    viewModel: MainViewModel) {

    val titles = listOf("Блокнот","Список дел","Календарь")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()
    val todoList by viewModel.sortedItemsFlow.collectAsStateWithLifecycle()
        val item = viewModel.showDialog.item

        when(viewModel.showDialog.isWho){
          
            DELETE_DIALOG->{
                DeleteDialog {result->
                    if(result && item != null) viewModel.deleteItem(item)
                    viewModel.showDialog = DialogState()
                }
            }

            INSERT_DIALOG -> {
                AddOrChangeItemDialog(item) { returnedItem, result, alarm, delete ->
                    if (result && returnedItem != null) {

                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            var finalUri = returnedItem.uri

                            // 1. Если сработал флаг удаления и у старого элемента РЕАЛЬНО была картинка
                            if (delete && (item?.uri ?: "").isNotEmpty()) {
                                // Вызываем наш перенесенный нативный метод удаления
                               // deleteSavedImageKMP(item!!.uri)
                            }

                            // 2. Если картинка была изменена в диалоге, копируем её насовсем по твоей логике
                            if (delete && returnedItem.uri.isNotEmpty()) {
                                // Передаем временный путь returnedItem.uri и ID дела для перезаписи!
                               // finalUri = saveImagePermanentlyKMP(returnedItem.uri, returnedItem.id)
                            }

                            // 3. Отдаем полностью собранный объект во ViewModel для записи в Room
                            val itemToInsert = returnedItem.copy(uri = finalUri)
                            viewModel.insertItem(itemToInsert, alarm)
                        }

                    } else {
                        viewModel.showDialog = DialogState()
                    }
                }
            }
            NOTIFICATION->{
              CreateDateInAlarmDialog(viewModel)
            }
            TIME ->{
               if (item != null) {
            CreateTimeInAlarmDialog(date = item.alarmTime, viewModel = viewModel)
        } else {
                 viewModel.showDialog = DialogState()
                }  
            }

            ACTION ->{
              CreateActionInAlarmDialog(viewModel)
            }
        }
    


    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // ЖЕСТКО ЗАДАЕМ ВЫСОТУ 48.dp вместо IntrinsicSize.Min
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(

                onClick = { },

                modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

            ) {

                Icon(
                    imageVector = Icons.Default.Menu, // Нужен импорт androidx.compose.material.icons.Icons
                    contentDescription = "Меню",
                    tint = Color.White
                )

            }



            PrimaryTabRow(

                selectedTabIndex = pagerState.currentPage,

                modifier = Modifier.weight(1f),

                containerColor = Color.Transparent, // Прозрачный фон

                divider = {}, // Убираем стандартную линию снизу

                // Настройка полоски (индикатора)

                indicator = {

                    val modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
                    TabRowDefaults.PrimaryIndicator(

                        modifier = modifier,

                        width = 60.dp,        // Ширина полоски

                        height = 3.dp,        // Толщина

                        color = Color.White,  // БЕЛЫЙ ЦВЕТ
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)

                    )

                }

            ) {

                titles.forEachIndexed { index, title ->

                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = {
                            if (pagerState.currentPage != index) {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        },
                        text = {
                            Text(

                                color = if (selected) Color.White else Color.Gray,
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                // Дополнительно можно менять жирность
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )

                        }

                    )

                }

            }

        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()

        ) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    Notebook(viewModel,pageIndex)
                }
                1 -> {
                    ListToDo(list = todoList,
                        onClick = {item,action->
                            when(action){
                                ALARM->{viewModel.permission(NOTIFICATION,item)}
                                ALARM_LONG ->{viewModel.insertAlarmRepeat(item)}
                                IMAGE->{}
                                CHANGE_ITEM->{
                                    viewModel.showDialog = DialogState(INSERT_DIALOG,item)}
                                CHANGE->{
                                    val newItem = item.copy(
                                        change = !item.change,
                                        changeAlarm = false)
                                    viewModel.updateItem(newItem)
                                    if (item.changeAlarm) {
                                        viewModel.deleteAlarm(item.id)
                                        viewModel.deleteAlarm(item.id * -1)
                                    }
                                }
                                DELETE->{
                                    if(item.change)viewModel.deleteItem(item)
                                        else viewModel.showDialog = DialogState(DELETE_DIALOG,item)

                                }

                            }
                        },
                        onAddItem = {viewModel.showDialog = DialogState(INSERT_DIALOG)})
                }
                2 -> {Text("3")}

            }

        }

    }

}

