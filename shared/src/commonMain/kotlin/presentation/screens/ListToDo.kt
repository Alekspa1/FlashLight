package presentation.screens

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed 
import androidx.compose.foundation.lazy.rememberLazyListState 
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.room.Item
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import kotlinx.datetime.Instant
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.screens.CardItem
import presentation.theme.ThemeZabor

// ИМПОРТИРУЕМ ТОЛЬКО CALVIN REORDERABLE
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ListToDo(
    list: List<Item>,
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    onClick: (Item, Int) -> Unit = { _, _ -> },
    onAddItem: () -> Unit = {},
    onListReordered: (List<Item>) -> Unit = {}, 
    onDragDone: (List<Item>) -> Unit = {},       
    category: String = "Тест"
) {
    val listState = rememberLazyListState()
    var currentSnapshotList by remember(list) { mutableStateOf(list) }
    
   val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState

            val fromIdx = from.index - 1
            val toIdx = to.index - 1

            if (fromIdx in currentSnapshotList.indices && toIdx in currentSnapshotList.indices) {
                val updatedList = currentSnapshotList.toMutableList().apply {
                    add(toIdx, removeAt(fromIdx))
                }
                currentSnapshotList = updatedList // Обновляем локальный снимок
                onListReordered(updatedList) // Отдаем во ViewModel для UI
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
                items = list,
                key = { _, item -> item.id }
            ) { index, item ->
                val actualLazyColumnIndex = index + 1

               ReorderableItem(
    state = reorderableState,
    key = item.id
) { isDragging -> // 👈 Не забудь добавить 'isDragging ->'
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .draggableHandle(
                onDragStarted = {},
                onDragStopped = {
                    onDragDone(currentSnapshotList)
                }
            )
            .animateItem() 
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            CardItem(
                item = item,
                theme = theme,
                size = size
            ) { returnedItem, action ->
                onClick(returnedItem, action)
            }
        }
    }
}
            }
        }

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



@Preview(showBackground = true)
@Composable
fun ToDoListPreview() {
    // Наш фейковый список из 10 элементов для Студии
    val mockList = listOf(
        Item(id = 1, name = "Купить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую ленту", category = "Фокус", alarmTime = 1719750000000L, change = false, sort = 1),
        Item(id = 2, name = "Проверить Koin модули", category = "Фокус", alarmTime = 0L, change = false, sort = 2),
        Item(id = 3, name = "Починить затыки Skia на Windows", category = "Фокус", alarmTime = 1719753600000L, change = true, sort = 3, uri = "трололо", changeAlarm = false, desc = "Описание почему то может быть null"),
        Item(id = 4, name = "Похвалить себя за архитектуру", category = "Фокус", alarmTime = 0L, change = false, sort = 4),
        Item(id = 5, name = "Выпить кофе и размять спину", category = "Фокус", alarmTime = 0L, change = false, sort = 5),
        Item(id = 6, name = "Написать expect/actual для iOS", category = "Фокус", alarmTime = 1719760800000L, change = false, sort = 6, changeAlarm = true),
        Item(id = 7, name = "Протестировать Drag-and-Drop", category = "Фокус", alarmTime = 0L, change = true, sort = 7),
        Item(id = 8, name = "Удалить лишние .value из Flow", category = "Фокус", alarmTime = 0L, change = false, sort = 8),
        Item(id = 9, name = "Развернуть базу Room на десктопе", category = "Фокус", alarmTime = 1719771600000L, change = false, sort = 9),
        Item(id = 10, name = "Устроить киберпанк в интерфейсе", category = "Фокус", alarmTime = 0L, change = false, sort = 10)
    )

    // Вызываем твой экран списков и скармливаем ему этот муляж
    ListToDo(list = mockList, theme = ThemeZabor())
}


