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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.HorizontalDivider

// Для инлайнового текстового поля и стилей (BasicTextField, LocalTextStyle)
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item? = null,
    premium : Boolean = false,
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
    var errorMessage by remember {mutableStateOf(false)}
    
    LaunchedEffect(item) {
    if (item != null && item.id == 0 && item.uri.isNotEmpty() && originalFileName.isEmpty()) {
        originalFileName = "img_shared_${Clock.System.now().toEpochMilliseconds()}.jpg"
        }
    }
    LaunchedEffect(listSubItems) {
        listSubTask.clear()
        listSubTask.addAll(listSubItems)
        if(listSubTask.size >= 2 && !premium ) errorMessage = true
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
//        .border(
//            width = 1.dp,
//            color = theme.cardItemBorderFalse,
//            shape = RoundedCornerShape(10.dp)
//        )
) {
    // Внешний Box занимает всю ширину, чтобы прижать кнопку вправо
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Row теперь кликабелен сам по себе и сжимается под контент
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)) // Чтобы эффект нажатия не вылезал за границы
                .clickable { isExpanded = !isExpanded}
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
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp) // Даем отступы от внешних краев
) {
           if(errorMessage){
               Text(
                   text = "Больше подзадач доступно в PREMIUM версии",
                   color = MaterialTheme.colorScheme.error,
                   style = MaterialTheme.typography.bodyMedium,
                   modifier = Modifier.padding(start = 24.dp, top = 16.dp)
               )
           } else {
               BasicTextField(
                   value = stateTextSubTask,
                   onValueChange = { stateTextSubTask = it },
                   textStyle = LocalTextStyle.current.copy(color = theme.textColor, fontSize = 15.sp),
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(vertical = 8.dp),
                   decorationBox = { innerTextField ->
                       Row(
                           modifier = Modifier
                               .fillMaxWidth()
                               .border(1.dp, theme.textColor, RoundedCornerShape(8.dp))
                               .padding(horizontal = 12.dp, vertical = 10.dp),
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.SpaceBetween
                       ) {
                           Box(modifier = Modifier.weight(1f)) {
                               if (stateTextSubTask.isEmpty()) {
                                   Text("Добавить подзадачу...", color = theme.textDesc, fontSize = 15.sp)
                               }
                               innerTextField()



                           }
                           IconButton(
                               onClick = {
                                   val textSubItem =  if(stateTextSubTask.trim().isNotEmpty()) stateTextSubTask.trim() else "Без названия"
                                   //if (stateTextSubTask.isNotBlank()) {
                                       val maxSort = listSubTask.maxOfOrNull { it.sort } ?: -1
                                           listSubTask.add(
                                               SubItem(
                                                   idTask = item?.id ?: 0,
                                                   name = textSubItem,
                                                   change = false,
                                                   sort = maxSort + 1
                                               )
                                           )
                                           if(listSubTask.size >= 2 && !premium ) errorMessage = true


                                       stateTextSubTask = ""
                                  // }
                               },
                               modifier = Modifier.size(20.dp)
                           ) {
                               Icon(Icons.Default.Add, contentDescription = "Добавить", tint = theme.iconTint)
                           }
                       }
                   }
               )
           }



    if (listSubTask.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))

        // 2. Список подзадач с разделителями
        Column(modifier = Modifier.fillMaxWidth()) {
            listSubTask.forEachIndexed { index, subTask ->
                val isEditing = if (subTask.id != 0) {
                    editingSubTaskId == subTask.id
                } else {
                editingSubTaskId == -(index + 1)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), // Увеличили кликабельную высоту строки
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = subTask.name,
                            onValueChange = { newText -> listSubTask[index] = subTask.copy(name = newText) },
                            textStyle = LocalTextStyle.current.copy(color = theme.textColor, fontSize = 15.sp),
                            modifier = Modifier
                                .weight(1f)
                                 .border(1.dp, theme.borderCardMenuItem, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        )
                        IconButton(onClick = { editingSubTaskId = -1 }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Check, contentDescription = "ОК", tint = theme.cardItemBorderTrue)
                        }
                    } else {
                        Text(
                            text = subTask.name,
                            color = theme.textColor,
                            fontSize = 15.sp,
                            modifier = Modifier
                                        .weight(1f)
                                        .clickable { editingSubTaskId = if (subTask.id != 0) subTask.id else -(index + 1) }
                        )
                        IconButton(onClick = {
                            listSubTask.remove(subTask)
                            if(listSubTask.size < 2 && !premium ) errorMessage = false
                                             }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Удалить", tint = theme.cardItemBorderFalse)
                        }
                    }
                }

                // 3. Тонкая разделительная линия между пунктами (кроме последнего элемента)
                if (index < listSubTask.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        theme.textColor.copy(alpha = 0.15f),
                    )
                }
            }
        }
    }
}
    }
}
HorizontalDivider(
    thickness = 1.dp,
    color = theme.textColor.copy(alpha = 0.15f),
    modifier = Modifier.padding(horizontal = 16.dp) // Исправили 's' на 'z'
)


                KmpSpinnerInput(
                    selectedCategory = categorySelected,
                    list = listCategory,
                    theme = theme, // Передаем абстрактную тему дальше
                    onCategorySelected = { categorySelected = it }
                )
                
HorizontalDivider(
    thickness = 1.dp,
    color = theme.textColor.copy(alpha = 0.15f),
    modifier = Modifier.padding(horizontal = 16.dp) // Исправили 's' на 'z'
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
HorizontalDivider(
    thickness = 1.dp,
    color = theme.textColor.copy(alpha = 0.15f),
    modifier = Modifier.padding(horizontal = 16.dp) // Исправили 's' на 'z'
)
                
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val text = stateTextName.trim().ifEmpty { "Без названия" }

                    onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,calendar,false,originalFileName,date,listSubTask)
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
                        onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,calendar,true,originalFileName,date,listSubTask)
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
    onClick = { expanded = !expanded },
) {
    Row(
        modifier = Modifier
            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            .padding(start = 16.dp), 
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // Добавляем приглушенное пояснение
        Text(
            text = "Категория: ",
            fontSize = 15.sp,
            color = theme.textDesc // Твой серый цвет из темы
        )
        
        // Само название категории остается ярким
        Text(
            text = selectedCategory,
            fontSize = 15.sp,
            color = theme.textColor, // Твой белый/основной цвет
            fontWeight = FontWeight.Bold, // Сделаем чуть жирнее для акцента
            modifier = Modifier.padding(end = 8.dp)
        )
        
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
    }
}

            // TextButton(
            //     onClick = {expanded = !expanded },

            //     ) {
            //     Row(
            //         modifier = Modifier
            //             .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            //             .padding(start = 16.dp), // Отступ только слева, чтобы не зажимать клик
            //         verticalAlignment = Alignment.CenterVertically,
            //         horizontalArrangement = Arrangement.End
            //     ) {
            //         Text(
            //             text = selectedCategory,

            //             fontSize = 15.sp,
            //             modifier = Modifier.padding(end = 8.dp)
            //         )
            //         ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            //     }

            // }


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
