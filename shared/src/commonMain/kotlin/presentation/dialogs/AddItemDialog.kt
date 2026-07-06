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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item?,
    onCancel : ()-> Unit,
    onSave :(
        item: Item?,
        name: String,
        desc: String,
        uri: String,
        category: String,
        alarlm : Boolean,
            ) -> Unit ={_,_,_,_,_,_->},
    ){
    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var openImageState by remember { mutableStateOf(false) }
    var selectedFileUri: String by remember { mutableStateOf(item?.uri ?: "") }
    var categorySelected by remember { mutableStateOf(item?.category ?: "Повседневные") }
    val fileLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->
        selectedFileUri = (if (file != null) {
        parsePlatformUri(file)
        } else "")
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        withFrameMillis { }  // Ждем, пока отрендерится первый кадр окна
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    if (openImageState) {
        OpenImage(selectedFileUri){openImageState = false}
    }

    AlertDialog(
            onDismissRequest = { onCancel()}, // когда кудато нажал
            title = { Text("Сфокусироваться") },

            text = {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth() // Настройка ширины применится всегда
                            .then(
                                if (item == null) Modifier.focusRequester(focusRequester) else Modifier
                            ),
                            value = stateTextName,
                        onValueChange = { newText ->
                            stateTextName = newText
                        },
                        shape = RoundedCornerShape(10.dp),
                        label = {Text(text = "Название", color = Color.Gray)},

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                        )
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = stateTextDecs,
                        onValueChange = { newText ->
                            stateTextDecs = newText
                        },
                        shape = RoundedCornerShape(10.dp),
                        label = {Text("Описание",color = Color.Gray)},

                        // НАСТРАИВАЕМ ЦВЕТА И ВАШУ НЕОНОВУЮ СТИЛИСТИКУ:
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                        )
                    )

                    // Блок работы с картинкой (ImageView + Кнопки добавить/удалить)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedFileUri != "" ) {
                            // Отображение картинки через Coil (Замена Glide)
                            AsyncImage(
                                 model = selectedFileUri ,
                                contentDescription = "Превью фото",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                       
                                        openImageState = true }, // Клик открывает на весь экран
                                contentScale = ContentScale.Crop,
                            )
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.SpaceBetween){
                                TextButton(onClick = { fileLauncher.launch() }) {
                                    Text(text = "Изменить фото")
                                }
                                TextButton(onClick = { selectedFileUri = "" }) {
                                    Text(
                                        text = "Удалить фото",
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }


                            }
                            // Кнопка удаления фото (deleteText)

                        } else {
                            // Кнопка добавления фото (addPhoto)
                            Button(onClick = { fileLauncher.launch() }) {
                                Text(text = "Добавить фото")
                            }
                        }
                    }

                    // ТУТ БУДЕТ ТВOЙ СПИННЕР КАТЕГОРИЙ (В Compose это ExposedDropdownMenuBox)
                    // Оставим пока заглушку, чтобы не раздувать код
                    Text("Категория: $categorySelected", modifier = Modifier.padding(top = 8.dp))
                } },

            confirmButton = {
                TextButton(onClick = {
                    val text = stateTextName.trim().ifEmpty { "Без названия" }
                    onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,false)
                }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Отступ между кнопками
                ) {
                    TextButton(onClick = {
                        val text = stateTextName.trim().ifEmpty { "Без названия" }
                        onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,true)
                    }) {
                        Text("Установка будильника")
                    }
                    TextButton(onClick = { onCancel()}) {
                        Text("Отмена")
                    }
                }
            },
        )
}

@Composable
fun OpenImage(uri: String,onDismiss : () -> Unit){

    if (uri != "") {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // Позволяет картинке занять всю ширину экрана без полей AlertDialog
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() } // Закрытие при клике в любое место экрана
                    .padding(16.dp), // Небольшой отступ от краев дисплея для красоты
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Крупный план",
                    modifier = Modifier
                        .fillMaxWidth() // Растягивается по ширине
                        .wrapContentHeight(), // Высота подстроится автоматически и корректно
                     .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit // Картинка гарантированно поместится целиком без обрезки
                )
            }
        }
    }
}

expect fun parsePlatformUri(uri: PlatformFile?): String


//@Preview(showBackground = true)
//@Composable
//fun PreviewAddItemDialog(){
//    AddOrChangeItemDialog(null)
//}
