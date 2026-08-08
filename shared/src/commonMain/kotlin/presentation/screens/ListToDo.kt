package presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed 
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.setValue 
import androidx.compose.runtime.remember 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.room.model.Item
import data.room.model.ItemWithSubItems
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.Size
import presentation.theme.SizeNormal
import androidx.compose.ui.graphics.graphicsLayer

// ИМПОРТЫ СТАБИЛЬНОЙ БИБЛИОТЕКИ CALVIN REORDERABLE
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ListToDo(
    list: List<ItemWithSubItems>, // Принимаем связку с подзадачами
    selectedFileUri: (String) -> String = { _ -> "" },
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    isDragDropEnabled: Boolean = true,
    onClick: (Item, Int) -> Unit = { _, _ -> },
    onAddItem: () -> Unit = {},
    // Возвращаем назад чистый List<Item> для твоей ViewModel, как ты и просил!
    onDragDropped: (List<Item>) -> Unit = {}, 
    onSubDragDropped: (List<SubItem>) -> Unit = {},
    category: String = "Тест"
) {
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    // Вытаскиваем только дела для работы Drag-and-Drop, чтобы ничего не ломать
    val itemsOnly = remember(list) { list.map { it.item } }
    var currentSnapshotList by remember { mutableStateOf<List<Item>>(itemsOnly) }

    LaunchedEffect(list) {
        if (currentSnapshotList != itemsOnly) {
            currentSnapshotList = itemsOnly
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

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            val categoryName = category
            item(key = categoryName) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = categoryName,
                        color = theme.textColor,
                        fontSize = size.textMenu,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            itemsIndexed(
                items = currentSnapshotList,
                key = { _, item -> item.id } // Ключ работает по ID дела, всё чётко
            ) { index, item ->
                
                // Находим подзадачи для текущей карточки из исходного списка на лету
                val subItems = list.find { it.item.id == item.id }?.subItems ?: emptyList()

                ReorderableItem(
                    state = reorderableState,
                    key = item.id
                ) { isDragging ->
                    val draggableHandle = Modifier.longPressDraggableHandle(
                        enabled = isDragDropEnabled,
                        onDragStarted = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                        onDragStopped = {
                            val listWithUpdatedSort = currentSnapshotList.mapIndexed { idx, listItem -> 
                                listItem.copy(sort = idx) 
                            }
                            currentSnapshotList = listWithUpdatedSort
                            onDragDropped(listWithUpdatedSort) // Возвращаем чистый List<Item> во ViewModel
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .graphicsLayer { alpha = if (isDragging) 0.5f else 1f }
                    ) {
                        CardItem(
                            item = item,
                            listSubItems = subItems, // Передаем найденные подзадачи!
                            selectedFileUri = selectedFileUri(item.uri),
                            theme = theme,
                            dragModifier = draggableHandle,
                            size = size,
                            onClick = { returnedItem, action ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onClick(returnedItem, action)
                            },
                            onSubDragDropped = {list-> onSubDragDropped(list)}
                        )
                    }
                }
            }
        }

        // Блок нижних кнопок (Микрофон и Плюс)
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                IconButton(
                    modifier = Modifier.size(50.dp).align(Alignment.Center),
                    onClick = { },
                ) {
                    Image(
                        painter = theme.iconMicro(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
                    onClick = { onAddItem() },
                ) {
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
// fun ListToDo(
//     list: List<ItemWithSubItems>,
//     selectedFileUri: (String) -> String = { _ -> "" },
//     theme: Theme = ThemeNeon(),
//     size: Size = SizeNormal(),
//     isDragDropEnabled: Boolean = true, 
//     onClick: (Item, Int) -> Unit = { _, _ -> },
//     onAddItem: () -> Unit = {},
//     onDragDropped: (List<Item>) -> Unit = {}, 
//     category: String = "Тест"
// ) {
//     val listState = rememberLazyListState()

//     // Наш локальный "адаптер" в памяти Compose
//     val itemsOnly = remember(list) { list.map { it.item } }
//     var currentSnapshotList by remember { mutableStateOf<List<Item>>(itemsOnly) }
//     val haptic = LocalHapticFeedback.current
//     // Рубеж защиты: обновляем экран только если из БД прилетел реально измененный чекбокс
//     androidx.compose.runtime.LaunchedEffect(list) {
//         if (currentSnapshotList != list) {
//             currentSnapshotList = list
//         }
//     }

//     val reorderableState = rememberReorderableLazyListState(
//         lazyListState = listState,
//         onMove = { from, to ->
//             if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState

//             val fromIdx = from.index - 1
//             val toIdx = to.index - 1

//             if (fromIdx in currentSnapshotList.indices && toIdx in currentSnapshotList.indices) {
//                 val updatedList = currentSnapshotList.toMutableList().apply {
//                     add(toIdx, removeAt(fromIdx))
//                 }
//                 currentSnapshotList = updatedList 
//             }
//         }
//     )

//     Column(modifier = Modifier.fillMaxSize()) {
//         LazyColumn(
//             state = listState,
//             modifier = Modifier.fillMaxWidth().weight(1f),
//             verticalArrangement = Arrangement.spacedBy(5.dp), 
//         ) {
//             val categoryName = category
//             item(key = categoryName) {
//                 Box(modifier = Modifier.fillMaxWidth()) {
//                     Text(
//                         text = categoryName,
//                         color = theme.textColor,
//                         fontSize = size.textMenu,
//                         fontWeight = FontWeight.Bold,
//                         modifier = Modifier.align(Alignment.Center)
//                     )
//                 }
//             }

//             itemsIndexed(
//                 items = currentSnapshotList, 
//                 key = { _, item -> item.item.id }
//             ) { index, item ->

//                 ReorderableItem(
//                     state = reorderableState,
//                     key = item.id
//                 ) { isDragging -> 
//                     // Формируем чистый dragModifier для проброса внутрь карточки
//                     val draggableHandle = Modifier.longPressDraggableHandle(
//                         enabled = isDragDropEnabled,
//                         onDragStarted = {haptic.performHapticFeedback(HapticFeedbackType.LongPress)},
//                         onDragStopped = {
//                             // Пересчитываем sort строго в момент отпускания пальца
//                             val listWithUpdatedSort = currentSnapshotList.mapIndexed { idx, listItem ->
//                                 listItem.copy(sort = idx)
//                             }
//                             currentSnapshotList = listWithUpdatedSort // Фиксируем новые sort локально на экране
//                             onDragDropped(listWithUpdatedSort) // Отправляем готовый список во ViewModel для Room
//                         }
                       
//                     )

//                     Box(
//                         modifier = Modifier
//                             .fillMaxWidth()
//                             .animateItem()
//                             .graphicsLayer {
//                                 alpha = if (isDragging) 0.5f else 1f
//                             }
//                     ) {
//                         CardItem(
//                             item = item.item,
//                             listSubItems = item.subItems,
//                             selectedFileUri = selectedFileUri(item.uri), 
//                             theme = theme,
//                             dragModifier = draggableHandle, // Наш готовый модификатор
//                             size = size,
//                             onClick = { 
//                                 returnedItem, action ->
//                                 haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                                 onClick(returnedItem, action) }
//                         )
//                     }
//                 }
//             }
//         }

//         Row(modifier = Modifier.fillMaxWidth()) {
//             Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
//                 IconButton(
//                     modifier = Modifier.size(50.dp).align(Alignment.Center),
//                     onClick = { },
//                 ) {
//                     Image(
//                         painter = theme.iconMicro(),
//                         contentDescription = null,
//                         modifier = Modifier.fillMaxSize()
//                     )
//                 }

//                 IconButton(
//                     modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
//                     onClick = { onAddItem() },
//                 ) {
//                     Icon(
//                         imageVector = theme.iconAdd,
//                         contentDescription = null,
//                         modifier = Modifier.fillMaxSize(),
//                         tint = theme.iconAddTint
//                     )
//                 }
//             }
//         }
//     }
// }



// @Preview(showBackground = true)
// @Composable
// fun ToDoListPreview() {
//     // Наш фейковый список из 10 элементов для Студии
//     val mockList = listOf(
//         Item(id = 1, name = "Купить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую ленту", category = "Фокус", alarmTime = 1719750000000L, change = false, sort = 1),
//         Item(id = 2, name = "Проверить Koin модули", category = "Фокус", alarmTime = 0L, change = false, sort = 2),
//         Item(id = 3, name = "Починить затыки Skia на Windows", category = "Фокус", alarmTime = 1719753600000L, change = true, sort = 3, uri = "трололо", changeAlarm = false, desc = "Описание почему то может быть null"),
//         Item(id = 4, name = "Похвалить себя за архитектуру", category = "Фокус", alarmTime = 0L, change = false, sort = 4),
//         Item(id = 5, name = "Выпить кофе и размять спину", category = "Фокус", alarmTime = 0L, change = false, sort = 5),
//         Item(id = 6, name = "Написать expect/actual для iOS", category = "Фокус", alarmTime = 1719760800000L, change = false, sort = 6, changeAlarm = true),
//         Item(id = 7, name = "Протестировать Drag-and-Drop", category = "Фокус", alarmTime = 0L, change = true, sort = 7),
//         Item(id = 8, name = "Удалить лишние .value из Flow", category = "Фокус", alarmTime = 0L, change = false, sort = 8),
//         Item(id = 9, name = "Развернуть базу Room на десктопе", category = "Фокус", alarmTime = 1719771600000L, change = false, sort = 9),
//         Item(id = 10, name = "Устроить киберпанк в интерфейсе", category = "Фокус", alarmTime = 0L, change = false, sort = 10)
//     )

//     // Вызываем твой экран списков и скармливаем ему этот муляж
//     ListToDo(list = mockList, theme = ThemeZabor())
// }


