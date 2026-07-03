package presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.room.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(
    item: Item?,
    onResult :(item: Item?,result: Boolean,alarm: Boolean) -> Unit ={_,_,_->},){

    var stateTextName by remember { mutableStateOf(item?.name ?: "") }
    var stateTextDecs by remember { mutableStateOf(item?.desc ?: "") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        withFrameMillis { }  // Ждем, пока отрендерится первый кадр окна
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // 2. Получаем контроллер клавиатуры


    AlertDialog(
            onDismissRequest = { onResult(null,false,false) }, // когда кудато нажал
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


                } },

            confirmButton = {
                TextButton(onClick = {
                    val text = if (stateTextName.trim().isEmpty())  "Без названия" else stateTextName.trim()
                    if (item != null){ onResult(item.copy(name = text,
                        desc = stateTextDecs.trim()),true,false)}
                    else{
                        val item = Item(name = text, desc = stateTextDecs.trim())
                        onResult(item,true,false)
                    }


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
                            desc = stateTextDecs.trim()),true,true)}
                        else{
                            val item = Item(name = text, desc = stateTextDecs.trim())
                            onResult(item,true,true)
                        }
                    }) {
                        Text("Установка будильника")
                    }

                    // Стандартная кнопка "Нет"
                    TextButton(onClick = { onResult(null,false,false) }) {
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