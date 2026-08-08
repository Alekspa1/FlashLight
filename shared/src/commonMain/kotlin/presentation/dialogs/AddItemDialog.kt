package presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import data.room.model.Item
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlin.time.Clock

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.tooling.preview.Preview
import presentation.theme.ThemeNeon
import presentation.theme.Theme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.IconButton // или material, зависит от вашего проекта
import androidx.compose.material3.Icon       // или material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import data.room.model.SubItem
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item? = null,
    onCancel : ()-> Unit = {},
    listCategory : List<String> = emptyList(),
    listSubItems : List<SubItem> = emptyList(),
    calendar : Boolean = false,
    date: Long = 0L,
    category: String = "Повседневные",
    theme: Theme = ThemeNeon(), // Используйте имя вашего БАЗОВОГО класса/интерфейса тем!
    onSave :(
        item: Item?,
        name: String,
        desc: String,
        uri: String,
        category: String,
        calendar : Boolean,
        alarlm : Boolean, // Оставил старое имя, чтобы не ломать лямбду в INSERT_DIALOG_ITEM
        originalNameImage : String,
        date: Long,
        newListSubItems: List<SubItem>
    ) -> Unit ={_,_,_,_,_,_,_,_,_,_->},
    getUri : (String) -> String = {""},
) {
    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var stateTextSubTask by remember {mutableStateOf("")}
    var isExpanded by remember { mutableStateOf(false) }
    var openImageState by remember { mutableStateOf(false) }
    var selectedFileUri: String by remember { mutableStateOf(getUri(item?.uri ?: "")) }
    var originalFileName by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf(item?.category ?: if(calendar) "Повседневные" else category) }
    val listSubTask = remember { mutableStateListOf<SubItem>() }
    var editingSubTaskId by remember { mutableIntStateOf(-1) }
    LaunchedEffect(listSubItems) {
        listSubTask.clear()
        listSubTask.addAll(listSubItems)
    }

    val fileLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
         if (file != null) {
             selectedFileUri = parsePlatformUri(file)
             originalFileName = "img_${Clock.System.now().toEpochMilliseconds()}.jpg"
        }
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        withFrameMillis { }
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    if (openImageState) {
        OpenImage(selectedFileUri){ openImageState = false }
    }

    AlertDialog(
        onDismissRequest = { onCancel() },

        title = { Text("Сфокусироваться") },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState()),
               
            ) {
                // Поле Название
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (item == null) Modifier.focusRequester(focusRequester) else Modifier),
                    value = stateTextName,
                    onValueChange = { stateTextName = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Название") },
                )

                // Поле Описание
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = stateTextDecs,
                    onValueChange = { stateTextDecs = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text("Описание") },
                )

               Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        // .border(
        //     width = 1.dp,
        //     color = theme.borderCardMenuItem,
        //     shape = RoundedCornerShape(10.dp)
        // )
) {
    // Внешний Box занимает всю ширину, чтобы прижать кнопку вправо
    Box(
        modifier = Modifier
            .fillMaxWidth()
        contentAlignment = Alignment.CenterEnd
    ) {
        // Row теперь кликабелен сам по себе и сжимается под контент
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)) // Чтобы эффект нажатия не вылезал за границы
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 12.dp, vertical = 8.dp), // Внутренние отступы самой кнопки
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Подзадачи",
                fontSize = 15.sp,
                color = theme.textColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
        }
    }

    // Выпадающее содержимое (остается без изменений)
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            // Поле ввода подзадачи (внутри рамки)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = stateTextSubTask,
                onValueChange = { stateTextSubTask = it },
                shape = RoundedCornerShape(10.dp),
                label = { Text("Подзадача") },
                trailingIcon = { 
                    IconButton(onClick = { 
                        if (stateTextSubTask.isNotBlank()) {
                            val maxSort = listSubTask.maxOfOrNull { it.sort } ?: -1
                            val newSortIndex = maxSort + 1

                            listSubTask.add(
                                SubItem(
                                    idTask = item?.id ?: 0,
                                    name = stateTextSubTask,
                                    change = false,
                                    sort = newSortIndex
                                )
                            )
                            stateTextSubTask = "" 
                        }
                    }) { 
                        Icon(Icons.Default.Add, contentDescription = "Добавить") 
                    } 
                }
            )

            // Список подзадач (если он не пустой, добавляем отступ сверху)
            if (listSubTask.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(modifier = Modifier.fillMaxWidth()
                              .border(
            width = 1.dp,
            color = theme.cardItemBorderFalse,
            shape = RoundedCornerShape(10.dp)
        )) {
Column(modifier = Modifier.fillMaxWidth()) {
    listSubTask.forEachIndexed { index, subTask ->
        val isEditing = editingSubTaskId == subTask.id && subTask.id != 0 
                || (subTask.id == 0 && editingSubTaskId == index + 100000) // Хитрый ключ для новых подзадач, у которых id еще 0

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isEditing) {
                // РЕЖИМ РЕДАКТИРОВАНИЯ: Поле ввода вместо статичного текста
                BasicTextField(
                    value = subTask.name,
                    onValueChange = { newText ->
                        listSubTask[index] = subTask.copy(name = newText)
                    },
                    textStyle = LocalTextStyle.current.copy(color = theme.textColor, fontSize = 15.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp, end = 8.dp)
                        .border(1.dp, theme.tintPremiumOn, RoundedCornerShape(4.dp)) // Подсвечиваем рамкой цвет темы
                        .padding(8.dp)
                )

                // Кнопка сохранения правок (Галочка)
                IconButton(
                    onClick = { editingSubTaskId = -1 }, // Выходим из режима редактирования
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, // Нужен импорт androidx.compose.material.icons.filled.Check
                        contentDescription = "Сохранить правку",
                        tint = theme.tintPremiumOn
                    )
                }
            } else {
                // ОБЫЧНЫЙ РЕЖИМ: Просто текст подзадачи
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 8.dp)
                        .weight(1f)
                        .clickable { 
                            // По клику на текст включаем редактирование этой строки
                            editingSubTaskId = if (subTask.id != 0) subTask.id else index + 100000 
                        },
                    text = subTask.name,
                    color = theme.textColor,
                    fontSize = 15.sp
                )

                // Кнопка удаления (Крестик)
                IconButton(
                    onClick = { listSubTask.remove(subTask) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Удалить подзадачу",
                        tint = theme.textDesc
                    )
                }
            }
        }
    }
}
                }
            }
        }
    }
}


                
//                 // Поле подзадачи
//                 Box(
//                     modifier = Modifier.fillMaxWidth(),
//                     contentAlignment = Alignment.CenterEnd // Прижимает содержимое к правому краю
//                 ) {
//                     TextButton(
//                         onClick = { isExpanded = !isExpanded },
//                     ) {
//                         Row(
//                             modifier = Modifier.padding(start = 16.dp),
//                             verticalAlignment = Alignment.CenterVertically,
//                             horizontalArrangement = Arrangement.End
//                         ) {
//                             Text(
//                                 text = "Подзадачи", // Добавлены кавычки
//                                 fontSize = 15.sp,
//                                 modifier = Modifier.padding(end = 8.dp)
//                             )
//                             ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
//                         }
//                     }
//                 }

// AnimatedVisibility(
//     visible = isExpanded, // Исправлено на одну переменную
//     enter = expandVertically() + fadeIn(),
//     exit = shrinkVertically() + fadeOut()
// ) {
//     // ВСЕ элементы внутри AnimatedVisibility обязательно оборачиваем в Column
//     Column(modifier = Modifier.fillMaxWidth()) {
        
//         // Поле ввода подзадачи
//         OutlinedTextField(
//             modifier = Modifier.fillMaxWidth(),
//             value = stateTextSubTask,
//             onValueChange = { stateTextSubTask = it },
//             shape = RoundedCornerShape(10.dp),
//             label = { Text("Подзадача") },
//             trailingIcon = {
//                 IconButton(onClick = {
//                     val newSortIndex = maxSort + 1
//                     if (stateTextSubTask.isNotBlank()) {
//                         listSubTask.add(
//                             SubItem(
//                                 idTask = item?.id ?: 0, // ID родительского дела (если дело новое, тут будет 0)
//                                 name = stateTextSubTask, // Текст из вашего TextField "Подзадача"
//                                 change = false,
//                                 sort = newSortIndex
//                             )
//                         )
//                         stateTextSubTask = ""
//                     }
//                 }) {
//                     Icon(Icons.Default.Add, contentDescription = "Добавить")
//                 }
//             }
//         )

//         // Список добавленных подзадач
//         Column(modifier = Modifier.fillMaxWidth()) {
//             listSubTask.forEach { subTask ->
//                 Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(start = 6.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
//                     verticalAlignment = Alignment.CenterVertically,
//                 ) {
//                     Text(
//                         modifier = Modifier
//                             .padding(start = 5.dp, end = 5.dp)
//                             .weight(1f),
//                         text = subTask.name,
//                     )
//                     IconButton(
//                         onClick = { 
//                             // Реализуем удаление или отметку выполнения
//                             listSubTask.remove(subTask) 
//                         },
//                         modifier = Modifier
//                             .padding(end = 8.dp)
//                             .size(24.dp)
//                     ) {
//                         Icon(
//                             modifier = Modifier.fillMaxSize(),
//                             imageVector = Icons.Default.Close,
//                             contentDescription = "Check",
//                             tint = theme.chekBoxTint
//                         )
//                     }
//                 }
//             }
//         }
//     }
// }

                KmpSpinnerInput(
                    selectedCategory = categorySelected,
                    list = listCategory,
                    theme = theme, // Передаем абстрактную тему дальше
                    onCategorySelected = { categorySelected = it }
                )

                // Блок работы с фото
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedFileUri.isNotEmpty()) {
                        AsyncImage(
                            model = selectedFileUri,
                            contentDescription = "Превью фото",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { openImageState = true },
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { fileLauncher.launch() },
                               // colors = ButtonDefaults.textButtonColors(contentColor = theme.textColor)
                            ) {
                                Text(text = "Изменить фото")
                            }
                            TextButton(
                                onClick = { 
                                    selectedFileUri = ""
                                    originalFileName = ""},
                                colors = ButtonDefaults.textButtonColors(contentColor = theme.cardItemBorderFalse)
                            ) {
                                Text(text = "Удалить фото")
                            }
                        }
                    } else {
                        // Пружина: забирает всё пространство слева и толкает кнопку вправо
                        Spacer(modifier = Modifier.weight(1f)) 
                        
                        TextButton(
                            onClick = { fileLauncher.launch() })
                        {
                            Text(text = "Добавить фото")
                        }
                    }
                }
                
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val text = stateTextName.trim().ifEmpty { "Без названия" }
                    onSave(item,text,stateTextDecs,selectedFileUri,categorySelected,calendar,false,originalFileName,date,listSubTask)
                },
            ) {
                Text("Ок", fontWeight = FontWeight.Bold)
            }
        },

        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        val text = stateTextName.trim().ifEmpty { "Без названия" }
                        onSave(item,text,stateTextDecs,selectedFileUri,categorySelected,calendar,true,originalFileName,date,listSubTask)
                    },

                ) {
                    Text("Установка будильника")
                }
                TextButton(
                    onClick = { onCancel() },

                ) {
                    Text("Отмена")
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KmpSpinnerInput(
    selectedCategory: String,            
    list: List<String>,                  
    theme: Theme = ThemeNeon(), 
    onCategorySelected: (String) -> Unit 
) {
    var expanded by remember { mutableStateOf(false) }

    // 1. РОДИТЕЛЬ (Всегда идет самым первым)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd // Прижимает содержимое к правому краю
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {  },
        ) {

            TextButton(
                onClick = {expanded = !expanded },

                ) {
                Row(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .padding(start = 16.dp), // Отступ только слева, чтобы не зажимать клик
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = selectedCategory,

                        fontSize = 15.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }

            }


            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                matchAnchorWidth = false
            ) {
                list.forEachIndexed { index, item -> // <--- Используем forEachIndexed вместо обычного forEach
                    DropdownMenuItem(
                        text = {
                            // Возвращаем сюда чистый текст без лишних колонок внутри
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        },
                        onClick = {
                            onCategorySelected(item)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            leadingIconColor = theme.iconAddTint,
                            trailingIconColor = theme.iconAddTint
                        )
                    )

                    // ПРОГРАММИРУЕМ РАЗДЕЛИТЕЛЬ:
                    // Рисуем полосу ПОСЛЕ элемента, только если это НЕ самый последний элемент в списке
                    if (index < list.lastIndex) {
                        HorizontalDivider(
                            thickness = 1.dp, // 2.dp обычно выглядит слишком жирно для разделителя, лучше 1.dp или 0.5.dp
                            color = theme.textDesc.copy(alpha = 0.3f), // Делаем полоску слегка прозрачной, чтобы она не резала глаз
                            modifier = Modifier.padding(horizontal = 8.dp) // Небольшой отступ по бокам, чтобы полоса не упиралась в края меню
                        )
                    }
                }
            }
           }

            // 3. ВЫПАДАЮЩИЙ СПИСОК (Появляется только при клике)

        }
    }

expect fun parsePlatformUri(uri: PlatformFile): String


@Preview(showBackground = true)
@Composable
fun PreviewAddItemDialog(){
    AddOrChangeItemDialog(listCategory = listOf("хер","тыква"))
}
