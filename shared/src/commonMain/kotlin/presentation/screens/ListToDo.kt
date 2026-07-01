package presentation.screens

import CommonConst.ADD
import CommonConst.ALARM
import CommonConst.CHANGE
import CommonConst.CHANGE_ITEM
import CommonConst.DELETE
import CommonConst.IMAGE
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import data.room.Item
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@Composable
fun ListToDo(list: List<Item>,
             theme: Theme = ThemeNeon(),
             onClick : (Item?, Int) -> Unit = { _, _->}){
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(

            modifier = Modifier.fillMaxWidth().weight(1f),

            verticalArrangement = Arrangement.spacedBy(8.dp),


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
                Box(modifier = Modifier.animateItem()) {
                    Item(item) { item, action -> onClick(item, action)
                    }
                }
            }

        }



        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(8.dp)

            ){
                IconButton(modifier = Modifier.align(Alignment.Center),
                    onClick = {  },
                ){
                    Image(
                        painter = painterResource(theme.iconMicro),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)

                        )
                }

                IconButton(modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onClick(null,ADD) },
                ){
                    Icon(
                        imageVector = theme.iconAdd,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = theme.iconAddTint

                    )
                }
            }

        }
    }




}

@Composable

fun Item(item: Item, theme: Theme = ThemeNeon(), onClick : (Item, Int) -> Unit = { _, _->}) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Все три элемента будут идеально ровно по центру высоты
        ) {
            // 1. Левая кнопка/текст

                IconButton(

                    onClick = { onClick(item,ALARM) },

                    modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                ) {

                    Icon(
                        imageVector = Icons.Default.Alarm, // Нужен импорт androidx.compose.material.icons.Icons
                        contentDescription = "Будильник",
                       tint =  if(item.changeAlarm) theme.tintAlarmOn else theme.tintAlarmOff,
                        modifier = Modifier.size(30.dp)
                    )

                }


            // 2. Центральная карточка (занимает всё оставшееся пространство)

            Card(
                modifier = Modifier
                    .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        2.dp,
                        if(item.change) theme.cardItemBorderTrue
                        else if (item.changeAlarm) theme.cardItemBorderAlarm
                        else theme.cardItemBorderFalse,
                        RoundedCornerShape(16.dp)
                    ).clickable{onClick(item,CHANGE_ITEM)},

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
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (item.uri != "") IconButton(

                        onClick = { onClick(item,IMAGE) },

                        //modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                    ) {

                        Icon(

                            imageVector = theme.iconImage, // Нужен импорт androidx.compose.material.icons.Icons
                            contentDescription = "Картинка"
                        )

                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = item.name,
                            color = theme.textColor)
                        if (item.desc!!.isNotEmpty())  { Text(text = item.desc, color = theme.textDecs) }
                        if (item.changeAlarm) Text(text = "Напоминт в четверг в 18:00", color = theme.textAlarm)
                    }
                    IconButton(

                        onClick = { onClick(item,CHANGE) },

                        modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                    ) {

                        Icon(
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

                    modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

                ) {

                    Icon(
                        imageVector = theme.iconDelItem, // Нужен импорт androidx.compose.material.icons.Icons
                        contentDescription = "Меню",
                        tint = theme.iconDelTint,
                        modifier = Modifier.size(30.dp)
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
        Item(id = 3, name = "Починить затыки Skia на Windows", category = "Фокус", alarmTime = 1719753600000L, change = true, sort = 3, uri = "трололо", desc = "Описание почему то может быть null"),
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


