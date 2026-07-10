package presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage

import data.room.Item
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlin.time.Clock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import presentation.theme.ThemeNeon
import presentation.theme.Theme
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.text.font.FontWeight
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun AddOrChangeItemDialog(
//     item: Item? = null,
//     onCancel : ()-> Unit = {},
//     listCategory : List<String> = emptyList(),
//     calendar : Boolean = false,
//     category: String = "Повседневные",
//     onSave :(
//         item: Item?,
//         name: String,
//         desc: String,
//         uri: String,
//         category: String,
//         alarlm : Boolean,
//         originalNameImage : String,
//             ) -> Unit ={_,_,_,_,_,_,_->},
//         getUri : (String) -> String = {""},
//     ){
//     var stateTextName by remember { mutableStateOf(item?.name ?: "") }
//     var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
//     var openImageState by remember { mutableStateOf(false) }
//     var selectedFileUri: String by remember { mutableStateOf(getUri(item?.uri ?: "")) }
//     var originalFileName by remember { mutableStateOf("") }
//     var categorySelected by remember { mutableStateOf(item?.category ?:
//     if(calendar) "Повседневные" else category) }
//     val fileLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
//          if (file != null) {
//              selectedFileUri =  parsePlatformUri(file)
//              originalFileName = "img_${Clock.System.now().toEpochMilliseconds()}.jpg"
//         }
//     }

//     val focusRequester = remember { FocusRequester() }
//     val keyboardController = LocalSoftwareKeyboardController.current
//     LaunchedEffect(Unit) {
//         withFrameMillis { }  // Ждем, пока отрендерится первый кадр окна
//         focusRequester.requestFocus()
//         keyboardController?.show()
//     }

//     if (openImageState) {
//         OpenImage(selectedFileUri){openImageState = false}
//     }

//     AlertDialog(
//             onDismissRequest = { onCancel()}, // когда кудато нажал
//             title = { Text("Сфокусироваться") },

//             text = {
//                 Column(modifier = Modifier.fillMaxWidth(),
//                     verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                     OutlinedTextField(
//                         modifier = Modifier
//                             .fillMaxWidth() // Настройка ширины применится всегда
//                             .then(
//                                 if (item == null) Modifier.focusRequester(focusRequester) else Modifier
//                             ),
//                             value = stateTextName,
//                         onValueChange = { newText ->
//                             stateTextName = newText
//                         },
//                         shape = RoundedCornerShape(10.dp),
//                         label = {Text(text = "Название", color = Color.Gray)},

//                         colors = OutlinedTextFieldDefaults.colors(
//                             focusedTextColor = Color.Black,
//                             unfocusedTextColor = Color.Black,
//                         )
//                     )
//                     OutlinedTextField(
//                         modifier = Modifier
//                             .fillMaxWidth(),
//                         value = stateTextDecs,
//                         onValueChange = { newText ->
//                             stateTextDecs = newText
//                         },
//                         shape = RoundedCornerShape(10.dp),
//                         label = {Text("Описание",color = Color.Gray)},

//                         // НАСТРАИВАЕМ ЦВЕТА И ВАШУ НЕОНОВУЮ СТИЛИСТИКУ:
//                         colors = OutlinedTextFieldDefaults.colors(
//                             focusedTextColor = Color.Black,
//                             unfocusedTextColor = Color.Black,
//                         )
//                     )

//                     // Блок работы с картинкой (ImageView + Кнопки добавить/удалить)
//                     Row(
//                         modifier = Modifier.fillMaxWidth(),
//                         horizontalArrangement = Arrangement.spacedBy(16.dp),
//                         verticalAlignment = Alignment.CenterVertically
//                     ) {
//                         if (selectedFileUri != "" ) {
//                             // Отображение картинки через Coil (Замена Glide)
//                             AsyncImage(
//                                  model = selectedFileUri ,
//                                 contentDescription = "Превью фото",
//                                 modifier = Modifier
//                                     .size(80.dp)
//                                     .clip(RoundedCornerShape(12.dp))
//                                     .clickable {
                                       
//                                         openImageState = true }, // Клик открывает на весь экран
//                                 contentScale = ContentScale.Crop,
//                             )
//                             Column(modifier = Modifier
//                                 .fillMaxWidth()
//                                 .weight(1f),
//                                 horizontalAlignment = Alignment.End,
//                                 verticalArrangement = Arrangement.SpaceBetween){
//                                 TextButton(onClick = { fileLauncher.launch() }) {
//                                     Text(text = "Изменить фото")
//                                 }
//                                 TextButton(onClick = {
//                                     selectedFileUri = ""

//                                 }) {
//                                     Text(
//                                         text = "Удалить фото",
//                                         color = MaterialTheme.colorScheme.error,
//                                     )
//                                 }


//                             }
//                             // Кнопка удаления фото (deleteText)

//                         } else {
//                             // Кнопка добавления фото (addPhoto)
//                             Button(onClick = { fileLauncher.launch() }) {
//                                 Text(text = "Добавить фото")
//                             }
//                         }
//                     }




//                     // Отображаем наш KMP Спиннер, когда список загрузился
//                         KmpSpinnerInput(
//                             selectedCategory = categorySelected,
//                             list = listCategory,
//                             onCategorySelected = { selected ->
//                                 categorySelected = selected

//                             }
//                         )

//                 }
//                 } ,

//             confirmButton = {
//                 TextButton(onClick = {
//                     val text = stateTextName.trim().ifEmpty { "Без названия" }
//                     onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,false,originalFileName)
//                 }) {
//                     Text("Ок")
//                 }
//             },
//             dismissButton = {
//                 Row(
//                     horizontalArrangement = Arrangement.spacedBy(8.dp) // Отступ между кнопками
//                 ) {
//                     TextButton(onClick = {
//                         val text = stateTextName.trim().ifEmpty { "Без названия" }
//                         onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,true,originalFileName)
//                     }) {
//                         Text("Установка будильника")
//                     }
//                     TextButton(onClick = { onCancel()}) {
//                         Text("Отмена")
//                     }
//                 }
//             },
//         )
// }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item? = null,
    onCancel : ()-> Unit = {},
    listCategory : List<String> = emptyList(),
    calendar : Boolean = false,
    category: String = "Повседневные",
    theme: Theme = ThemeNeon(), // Используйте имя вашего БАЗОВОГО класса/интерфейса тем!
    onSave :(
        item: Item?,
        name: String,
        desc: String,
        uri: String,
        category: String,
        alarlm : Boolean, // Оставил старое имя, чтобы не ломать лямбду в INSERT_DIALOG_ITEM
        originalNameImage : String,
    ) -> Unit ={_,_,_,_,_,_,_->},
    getUri : (String) -> String = {""},
) {
    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var openImageState by remember { mutableStateOf(false) }
    var selectedFileUri: String by remember { mutableStateOf(getUri(item?.uri ?: "")) }
    var originalFileName by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf(item?.category ?: if(calendar) "Повседневные" else category) }
    
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
        containerColor = Color(0xFFD3D3D3),

        title = { Text("Сфокусироваться", color = theme.textColor) },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = theme.textColor,
                        unfocusedTextColor = theme.textColor,
                        focusedBorderColor = theme.iconAddTint, // Свечение при фокусе
                        unfocusedBorderColor = theme.cardItemBorderFalse.copy(alpha = 0.4f),
                        focusedLabelColor = theme.iconAddTint,
                        unfocusedLabelColor = theme.textDecs
                    )
                )

                // Поле Описание
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = stateTextDecs,
                    onValueChange = { stateTextDecs = it },
                    shape = RoundedCornerShape(10.dp),
                    label = { Text("Описание") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = theme.textColor,
                        unfocusedTextColor = theme.textColor,
                        focusedBorderColor = theme.iconAddTint,
                        unfocusedBorderColor = theme.cardItemBorderFalse.copy(alpha = 0.4f),
                        focusedLabelColor = theme.iconAddTint,
                        unfocusedLabelColor = theme.textDecs
                    )
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
                                .border(1.dp, theme.cardItemBorderFalse, RoundedCornerShape(12.dp))
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
                                colors = ButtonDefaults.textButtonColors(contentColor = theme.iconAddTint)
                            ) {
                                Text(text = "Изменить фото")
                            }
                            TextButton(
                                onClick = { selectedFileUri = "" },
                                colors = ButtonDefaults.textButtonColors(contentColor = theme.cardItemBorderFalse)
                            ) {
                                Text(text = "Удалить фото")
                            }
                        }
                    } else {
                        // Пружина: забирает всё пространство слева и толкает кнопку вправо
                        Spacer(modifier = Modifier.weight(1f)) 
                        
                        TextButton(
                            onClick = { fileLauncher.launch() },
                            colors = ButtonDefaults.textButtonColors(
                                
                                contentColor = theme.textColor
                            ),
                            modifier = Modifier.border(1.dp, theme.cardItemBorderFalse, RoundedCornerShape(10.dp))
                        ) {
                            Text(text = "Добавить фото")
                        }
                    }
                }

                // Спиннер
                KmpSpinnerInput(
                    selectedCategory = categorySelected,
                    list = listCategory,
                    theme = theme, // Передаем абстрактную тему дальше
                    onCategorySelected = { categorySelected = it }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val text = stateTextName.trim().ifEmpty { "Без названия" }
                    onSave(item, text, stateTextDecs.trim(), selectedFileUri, categorySelected, false, originalFileName)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = theme.cardItemBorderTrue)
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
                        onSave(item, text, stateTextDecs.trim(), selectedFileUri, categorySelected, true, originalFileName)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = theme.textAlarm)
                ) {
                    Text("Установка будильника")
                }
                TextButton(
                    onClick = { onCancel() },
                    colors = ButtonDefaults.textButtonColors(contentColor = theme.textDecs)
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
    theme: Theme = ThemeNeon(), // Твой базовый класс/интерфейс темы
    onCategorySelected: (String) -> Unit 
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true, 
            trailingIcon = { 
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                ) 
            },
            modifier = Modifier
                // В актуальном Material 3 нужно явно указывать тип анкора
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true) 
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            label = { Text("Категория") }, // Добавили красивый лейбл в стиле остальных полей
            
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = theme.textColor,
                unfocusedTextColor = theme.textColor,
                focusedBorderColor = theme.cardItemBorderFalse,
                unfocusedBorderColor = theme.cardItemBorderFalse.copy(alpha = 0.4f),
                focusedLabelColor = theme.iconAddTint,
                unfocusedLabelColor = theme.textDecs,
                focusedTrailingIconColor = theme.cardItemBorderFalse,
                unfocusedTrailingIconColor = theme.textDecs
            )
        )

        // Само выпадающее окно
       ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier 
                .background(Color(0xFF121214))
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = item, 
                            color = theme.textColor, // Белый текст
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    onClick = {
                        onCategorySelected(item) 
                        expanded = false
                    },
                    // Настройка эффекта пульсации/клика внутри меню
                    colors = MenuDefaults.itemColors(
                        textColor = theme.textColor,
                        // Цвет фона элемента при клике (делаем легкий неоновый отсвет)
                        leadingIconColor = theme.iconAddTint,
                        trailingIconColor = theme.iconAddTint
                    ),
                    // Небольшой внутренний отступ для аккуратности
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun KmpSpinnerInput(
//     selectedCategory: String,            // Текущее выбранное значение (всегда первое из списка на старте)
//     list: List<String>,                  // Ваш отсортированный во ViewModel список
//     onCategorySelected: (String) -> Unit // Колбэк изменения
// ) {
//     var expanded by remember { mutableStateOf(false) }

//     // Контейнер для выпадающего списка
//     ExposedDropdownMenuBox(
//         expanded = expanded,
//         onExpandedChange = { expanded = !expanded }
//     ) {
//         // Поле ввода, которое имитирует сам Спиннер
//         OutlinedTextField(
//             value = selectedCategory,
//             onValueChange = {},
//             readOnly = true, // Запрещаем ввод с клавиатуры
//             trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//             modifier = Modifier.menuAnchor().fillMaxWidth(),
//             colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
//         )

//         // Само выпадающее меню с элементами
//         ExposedDropdownMenu(
//             expanded = expanded,
//             onDismissRequest = { expanded = false }
//         ) {
//             list.forEach { item ->
//                 DropdownMenuItem(
//                     text = { Text(text = item) },
//                     onClick = {
//                         onCategorySelected(item) // Передаем наверх выбранную строку
//                         expanded = false
//                     }
//                 )
//             }
//         }
//     }
// }


expect fun parsePlatformUri(uri: PlatformFile): String


@Preview(showBackground = true)
@Composable
fun PreviewAddItemDialog(){
    AddOrChangeItemDialog(listCategory = listOf("хер","тыква"))
}
