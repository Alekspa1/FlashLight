package presentation.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.room.Item
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item?,
    onResult :(item: Item?,result: Boolean,alarm: Boolean,deleteUri : Boolean) -> Unit ={_,_,_,_->},){

    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var isImageExpanded by remember { mutableStateOf(false) }
    var uriPhoto by remember { mutableStateOf(item?.uri ?: "") }
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }
    var categorySelected by remember { mutableStateOf(item?.category ?: "Повседневные") }
 val fileLauncher = rememberFilePickerLauncher(type = PickerType.Image) { platformFile ->
    platformFile?.let { file ->
        val rawPath = file.path ?: ""
        
        // Исправляем путь ТОЛЬКО для Android, если это content:// ссылка
        uriPhoto = if (rawPath.startsWith("content://")) {
            rawPath // Coil на Android умеет читать content:// строку напрямую, если не путать его файловыми префиксами
        } else if (!rawPath.startsWith("file://") && !rawPath.contains(":/")) {
            "file://$rawPath" // Для обычных локальных путей
        } else {
            rawPath // Для Десктопа (там пути уже идут как C:/... или /Users/...)
        }
    }
}
    var deleteUri by remember { mutableStateOf(false) }

    if (isImageExpanded && uriPhoto.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { isImageExpanded = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                AsyncImage(
                    model = uriPhoto,
                    contentDescription = "Крупный план",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable { isImageExpanded = false }, // Закрыть по клику на картинку
                    contentScale = ContentScale.Fit
                )
            }
        )
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        withFrameMillis { }  // Ждем, пока отрендерится первый кадр окна
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // 2. Получаем контроллер клавиатуры


    AlertDialog(
            onDismissRequest = { onResult(null,false,false,deleteUri) }, // когда кудато нажал
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
                        if (uriPhoto != "") {
                            // Отображение картинки через Coil (Замена Glide)
                            AsyncImage(
                                model = selectedFile,
                                contentDescription = "Превью фото",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clickable { isImageExpanded = true }, // Клик открывает на весь экран
                                contentScale = ContentScale.Crop
                            )

                            // Кнопка удаления фото (deleteText)
                            Text(
                                text = "Удалить фото",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable { uriPhoto = "" }
                            )
                        } else {
                            // Кнопка добавления фото (addPhoto)
                            Button(onClick = { fileLauncher.launch() }) {
                                Text("Добавить фото")
                            }
                        }
                    }

                    // ТУТ БУДЕТ ТВOЙ СПИННЕР КАТЕГОРИЙ (В Compose это ExposedDropdownMenuBox)
                    // Оставим пока заглушку, чтобы не раздувать код
                    Text("Категория: $categorySelected", modifier = Modifier.padding(top = 8.dp))




                } },

            confirmButton = {
                TextButton(onClick = {
                    val text = if (stateTextName.trim().isEmpty())  "Без названия" else stateTextName.trim()
                    if(uriPhoto != item?.uri ) deleteUri = true

                    onResult(
                        item?.copy(name = text, desc = stateTextDecs.trim(), uri = uriPhoto)
                            ?: Item(name = text, desc = stateTextDecs.trim(), uri = uriPhoto),
                        true, // твой result
                        false, // твой alarm
                        deleteUri
                    )


                }) {
                    Text("Ок")
                }
            },

            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Отступ между кнопками
                ) {


                    TextButton(onClick = {
                        val text = if (stateTextName.trim().isEmpty())  "Без названия" else stateTextName.trim()
                        if (item != null){ onResult(item.copy(name = text,
                            desc = stateTextDecs.trim()),true,true,deleteUri)}
                        else{
                            val item = Item(name = text, desc = stateTextDecs.trim())
                            onResult(item,true,true,deleteUri)
                        }
                    }) {
                        Text("Установка будильника")
                    }

                    // Стандартная кнопка "Нет"
                    TextButton(onClick = { onResult(null,false,false,deleteUri) }) {
                        Text("Отмена")
                    }
                }
            },
        )


}


@Preview(showBackground = true)
@Composable
fun PreviewAddItemDialog(){
    AddOrChangeItemDialog(null)
}
