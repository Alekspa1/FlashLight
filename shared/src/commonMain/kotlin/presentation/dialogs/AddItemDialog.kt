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
        originalNameImage : String,
            ) -> Unit ={_,_,_,_,_,_,_->},
        getUri : (String) -> String,
    ){
    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }
    var openImageState by remember { mutableStateOf(false) }
    var selectedFileUri: String by remember { mutableStateOf(getUri(item?.uri ?: "")) }
    var originalFileName by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf(item?.category ?: "Повседневные") }
    val fileLauncher = rememberFilePickerLauncher(type = PickerType.Image) { file ->

         if (file != null) {
             selectedFileUri =  parsePlatformUri(file)
             originalFileName = "img_${Clock.System.now().toEpochMilliseconds()}.jpg"
        }
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
                                TextButton(onClick = {
                                    selectedFileUri = ""

                                }) {
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
                    onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,false,originalFileName)
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
                        onSave(item,text,stateTextDecs.trim(),selectedFileUri,categorySelected,true,originalFileName)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenImage(uri: String, onDismiss: () -> Unit) {

    if (uri.isNotEmpty()) {
        // 1. Состояния для хранения масштаба и координат сдвига картинки
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    // 2. Обычный клик по фону закроет диалог, ТОЛЬКО если картинка не увеличена
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Убираем вспышку клика на фоне для красоты
                        onClick = { if (scale == 1f) onDismiss() },
                        onDoubleClick = {
                            // Сброс по двойному тапу, если увеличена, или быстрый зум до 2x
                            if (scale > 1f) {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            } else {
                                scale = 2f
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Крупный план",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(12.dp))
                        // 3. Перехват жестов зума и панорамирования
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, gestureZoom, _ ->
                                // Ограничиваем масштаб от 1x до 4x
                                scale = (scale * gestureZoom).coerceIn(1f, 4f)

                                if (scale > 1f) {
                                    // Двигаем картинку вслед за пальцем с учетом текущего зума
                                    offsetX += pan.x * scale
                                    offsetY += pan.y * scale
                                } else {
                                    // Сбрасываем координаты в центр, если масштаб вернулся к 1x
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        }
                        // 4. Применяем трансформации на уровне GPU для максимальной плавности
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

expect fun parsePlatformUri(uri: PlatformFile): String


//@Preview(showBackground = true)
//@Composable
//fun PreviewAddItemDialog(){
//    AddOrChangeItemDialog(null)
//}
