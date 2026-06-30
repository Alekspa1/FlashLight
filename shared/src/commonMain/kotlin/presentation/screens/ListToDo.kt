package presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.room.Item
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.ic_del_notebook_neon
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.painterResource
import presentation.dialogs.DialogState

@Composable
fun ListToDo(list: List<Item>){
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(

            modifier = Modifier.fillMaxWidth().weight(1f),

            // Тот самый отступ между элементами, о котором мы говорили

            verticalArrangement = Arrangement.spacedBy(12.dp),

            // Отступы для всего списка (чтобы не прилипало к краям при скролле)

            contentPadding = PaddingValues(8.dp)

        ) {
            val categoryName = list.firstOrNull()?.category ?: "Повседневные"
            item(key = categoryName) {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)){
                    Text(
                        text = categoryName,
                        color = Color.White, // Твой фирменный сочный сине-голубой неон!
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            items(
                items = list,
                key = { it.id!!}
            ){item->
                Item(item)
            }

        }



        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)){
                IconButton(modifier = Modifier.align(Alignment.Center),
                    onClick = {  },
                ){
                    Image(
                        painter = painterResource(Res.drawable.ic_micro_neon),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)

                        )
                }

                IconButton(modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {  },
                ){
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF65D4FF)

                    )
                }
            }

        }
    }




}

@Composable

fun Item(item: Item) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Все три элемента будут идеально ровно по центру высоты
        ) {
            // 1. Левая кнопка/текст

                IconButton(

                    onClick = {  },

                    modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                ) {

                    Icon(
                        imageVector = Icons.Default.Alarm, // Нужен импорт androidx.compose.material.icons.Icons
                        contentDescription = "Меню",
                       tint =  if(item.changeAlarm) Color.Yellow else Color.White
                    )

                }


            // 2. Центральная карточка (занимает всё оставшееся пространство)

            Card(
                modifier = Modifier
                    .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        3.dp,
                        if (item.changeAlarm) Color.Yellow else Color.Green,
                        RoundedCornerShape(16.dp)
                    ),

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0x80ADD8E6) // Твой полупрозрачный неоновый цвет!
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (item.alarmText != "") IconButton(

                        onClick = {  },

                        //modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                    ) {

                        Icon(

                            imageVector = Icons.Default.Image, // Нужен импорт androidx.compose.material.icons.Icons
                            contentDescription = "Меню",
                            tint = Color.White
                        )

                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(item.name)
                        if (item.desc?.isNotEmpty() ?: true) item.desc?.let { Text(it) }
                        if (item.changeAlarm) Text(text = "item.alarmTime", color = Color.Yellow)
                    }
                    IconButton(

                        onClick = {  },

                        modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                    ) {

                        Icon(
                            imageVector = if(item.change) Icons.Default.CheckBoxOutlineBlank
                            else Icons.Default.CheckBox,
                            contentDescription = "Меню",
                            tint = Color.White
                        )

                    }
                }
            }



            // 3. Правая кнопка/текст



                IconButton(

                    onClick = {  },

                    modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                ) {

                    Icon(
                        imageVector = Icons.Default.Delete, // Нужен импорт androidx.compose.material.icons.Icons
                        contentDescription = "Меню",
                        tint = Color.White
                    )

                }



        } // Конец Row



    }

}

@Preview(showBackground = true)
@Composable
fun ToDoListPreview() {
    // Наш фейковый список из 10 элементов для Студии
    val mockList = listOf(
        Item(id = 1, name = "Купить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую лентуКупить неоновую ленту", category = "Фокус", alarmTime = 1719750000000L, change = false, sort = 1),
        Item(id = 2, name = "Проверить Koin модули", category = "Фокус", alarmTime = 0L, change = false, sort = 2),
        Item(id = 3, name = "Починить затыки Skia на Windows", category = "Фокус", alarmTime = 1719753600000L, change = true, sort = 3, alarmText = "трололо", desc = "Описание почему то может быть null"),
        Item(id = 4, name = "Похвалить себя за архитектуру", category = "Фокус", alarmTime = 0L, change = false, sort = 4),
        Item(id = 5, name = "Выпить кофе и размять спину", category = "Фокус", alarmTime = 0L, change = false, sort = 5),
        Item(id = 6, name = "Написать expect/actual для iOS", category = "Фокус", alarmTime = 1719760800000L, change = false, sort = 6, changeAlarm = true),
        Item(id = 7, name = "Протестировать Drag-and-Drop", category = "Фокус", alarmTime = 0L, change = true, sort = 7),
        Item(id = 8, name = "Удалить лишние .value из Flow", category = "Фокус", alarmTime = 0L, change = false, sort = 8),
        Item(id = 9, name = "Развернуть базу Room на десктопе", category = "Фокус", alarmTime = 1719771600000L, change = false, sort = 9),
        Item(id = 10, name = "Устроить киберпанк в интерфейсе", category = "Фокус", alarmTime = 0L, change = false, sort = 10)
    )

    // Вызываем твой экран списков и скармливаем ему этот муляж
    ListToDo(list = mockList)
}


