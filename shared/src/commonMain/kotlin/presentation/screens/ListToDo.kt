package presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed 
import androidx.compose.foundation.lazy.rememberLazyListState 
import androidx.compose.material3.Card
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
import data.room.Item
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.Size
import presentation.theme.SizeNormal

// ИМПОРТЫ СТАБИЛЬНОЙ БИБЛИОТЕКИ CALVIN REORDERABLE
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer 


@Composable
fun ListToDo(
    list: List<Item>, // Сюда заходит todoList by viewModel.sortedItemsFlow.collectAsStateWithLifecycle()
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    isDragDropEnabled: Boolean = true, // Включаем/выключаем драг динамически (viewModel.getSort() == SORT_USER)
    onClick: (Item, Int) -> Unit = { _, _ -> },
    onAddItem: () -> Unit = {},
    onDragDropped: (List<Item>) -> Unit = {}, // 👈 1. Переименовали колбэк, чтобы соответствовать твоей логике itemTouchDropped
    category: String = "Тест"
) {
         val listState = rememberLazyListState()

    // 1. Создаем локальный "адаптер". Теперь remember пустой — он НЕ будет слепо затирать память при каждом шорохе!
    var currentSnapshotList by remember { mutableStateOf<List<Item>>(list) }

    // 🌟 2. АНАЛОГ ТВОЕГО НА ТИВНОГО COLLECT С РУБЕЖОМ ЗАЩИТЫ:
    // Как только из БД (через аргумент list) прилетают данные, мы явно проверяем их, как в твоем фрагменте!
    LaunchedEffect(list) {
        // Проверяем: если порядок элементов и их контент на экране УЖЕ СОВПАДАЮТ с БД — игнорируем (return)
        // Точно так же, как твоя нативная строка: if (currentItems == rawDataList) return@collect
        if (currentSnapshotList == list) return@LaunchedEffect

        // Если данные реально отличаются (например, прилетел измененный чекбокс), 
        // принудительно обновляем наш локальный "адаптер", как это делал твой FastAdapterDiffUtil.set()
        currentSnapshotList = list
    }
   

    // Инициализируем стейт реордера
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            // ЗАЩИТА: Заголовок стоит на индексе 0. Игнорируем рокировки с ним.
            if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState

            val fromIdx = from.index - 1
            val toIdx = to.index - 1

            if (fromIdx in currentSnapshotList.indices && toIdx in currentSnapshotList.indices) {
                // 🌟 3. АНАЛОГ DragDropUtil.onMove: Меняем элементы местами ТОЛЬКО внутри нашего локального "адаптера"
                val updatedList = currentSnapshotList.toMutableList().apply {
                    add(toIdx, removeAt(fromIdx))
                }
                currentSnapshotList = updatedList // Обновляем экран, карточки плавно летят. ViewModel и БД в этот момент молчат!
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

            // Читаем элементы из живого currentSnapshotList, чтобы UI не зависел от диска Room во время движения
            itemsIndexed(
                items = currentSnapshotList, 
                key = { _, item -> item.id }
            ) { index, item ->

                ReorderableItem(
                    state = reorderableState,
                    key = item.id
                ) { isDragging -> 
                    val reorder = this
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                        .graphicsLayer {
             
                    // Если зажали — делаем прозрачность 0.5f, как в твоем touchHelper. 
                    // Отпустили — возвращаем 100% яркость (1f)
                    alpha = if (isDragging) 0.5f else 1f
                }
                    ) {
                      
                            CardItem(
                            item = item,
                                theme = theme,
                            size = size,
                            reorderableScope = reorder, 
                            isDragDropEnabled = isDragDropEnabled,
                            currentListSnapshot = currentSnapshotList, 
    
    // ИСПРАВЛЕНО: Теперь строчка на месте, логика полностью синхронизирована
    onDragDone = { finalList -> 
        if (finalList == list) return@CardItem
        currentSnapshotList = finalList // 🌟 Фиксируем новые sort локально на экране
        onDragDropped(finalList)        // Отправляем готовый список во ViewModel для Room
    },
    
    onClick = { returnedItem, action -> onClick(returnedItem, action) }
)
                        
                    }
                }
            }
        }

        // --- Нижний блок кнопок остается без изменений ---
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
// fun ListToDo(list: List<Item>,
//              theme: Theme = ThemeNeon(),
//              size : Size = SizeNormal(),
//              onClick : (Item, Int) -> Unit = {_,_->},
//              onAddItem : () -> Unit = {},
//              category : String = "Тест"
//              ){
//     Column(modifier = Modifier.fillMaxSize()) {
//         LazyColumn(

//             modifier = Modifier.fillMaxWidth().weight(1f),

//             verticalArrangement = Arrangement.spacedBy(5.dp),

//         ) {
//             val categoryName = category
//             item(key = categoryName) {
//                 Box(modifier = Modifier.fillMaxWidth()){
//                     Text(
//                         text = categoryName,
//                         color = theme.textColor,
//                         fontSize = size.textMenu,
//                         fontWeight = FontWeight.Bold,
//                         modifier = Modifier.align(Alignment.Center)
//                     )
//                 }
//             }
//             items(
//                 items = list,
//                 key = { it.id}
//             ){item->
//                 Box(modifier = Modifier.animateItem()) {
//                     CardItem(item = item,
//                         theme = theme,
//                         size = size) { item, action -> onClick(item, action)
//                     }
//                 }
//             }

//         }



//         Row(modifier = Modifier.fillMaxWidth()) {
//             Box(modifier = Modifier.fillMaxWidth()
//                 .padding(8.dp)

//             ){
//                 IconButton(modifier = Modifier.size(50.dp).align(Alignment.Center),
//                     onClick = {  },
//                 ){
//                     Image(
//                         painter = theme.iconMicro(),
//                         contentDescription = null,
//                         modifier = Modifier.fillMaxSize()

//                         )
//                 }

//                 IconButton(modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
//                     onClick = { onAddItem() },
//                 ){
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


