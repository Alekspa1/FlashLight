package presentation.dialogs

import androidx.compose.foundation.border
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import data.room.Item
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
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.IconButton // или material, зависит от вашего проекта
import androidx.compose.material3.Icon       // или material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item? = null,
    onCancel : ()-> Unit = {},
    listCategory : List<String> = emptyList(),
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
    ) -> Unit ={_,_,_,_,_,_,_,_,_->},
    getUri : (String) -> String = {""},
) {
    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var stateTextSubTask by remember {mutableStateOf("")}
    var openImageState by remember { mutableStateOf(false) }
    var selectedFileUri: String by remember { mutableStateOf(getUri(item?.uri ?: "")) }
    var originalFileName by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf(item?.category ?: if(calendar) "Повседневные" else category) }
    val listSubTask = remember { mutableStateListOf<String>() }
    
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
               
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
                // Поле подзадачи
OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = stateTextSubTask,
    onValueChange = { stateTextDecs = it },
    shape = RoundedCornerShape(10.dp),
    label = { Text("Подзадача") },
    trailingIcon = {
        IconButton(onClick = {
            if (stateTextSubTask.isNotBlank()) {
                listSubTask.add(stateTextDecs) // Добавляем в список
                stateTextSubTask = "" // Очищаем поле ввода
            }
        }) {
            Icon(Icons.Default.Add, contentDescription = "Добавить")
        }
    }
)
Column {
    listSubTask.forEach { subTask ->
        Text(text = subTask, modifier = Modifier.padding(vertical = 4.dp))
    }
}

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
                    onSave(item,text,stateTextDecs,selectedFileUri,categorySelected,calendar,false,originalFileName,date)
                },
               // colors = ButtonDefaults.textButtonColors(contentColor = theme.textColor)
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
                        onSave(item,text,stateTextDecs,selectedFileUri,categorySelected,calendar,true,originalFileName,date)
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
