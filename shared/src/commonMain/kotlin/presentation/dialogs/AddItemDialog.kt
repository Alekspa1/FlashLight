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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.room.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrChangeItemDialog(item: Item?, onResult :(Item?,Boolean,Boolean) -> Unit ={_,_,_->},){
    var stateTextName by mutableStateOf(item.let { item?.name ?: "" })
    var stateTextDecs by mutableStateOf(item.let { item?.desc ?: "" })
        AlertDialog(
            onDismissRequest = { onResult(null,false,false) },
            title = { Text("Сфокусироваться") },

            text = {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                            value = stateTextName,
                        onValueChange = { newText ->
                            stateTextName = newText
                        },
                        shape = RoundedCornerShape(10.dp),
                        label = {Text(text = "Название", color = Color.Gray)},

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
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
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        )
                    )


                } },

            confirmButton = {
                TextButton(onClick = {
                    if (item != null){ onResult(item.copy(name = stateTextName,
                        desc = stateTextDecs),true,false)}
                    else{
                        val item = Item(null
                            ,stateTextName,
                            false,
                            "",
                            0,false
                            ,false
                            ,0
                            ,"Повседневные"
                            ,stateTextDecs
                            ,0)
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
                    // Твоя новая ТРЕТЬЯ кнопка (например, для отмены или другого действия)
                    TextButton(onClick = { /* Твое действие для третьей кнопки */ }) {
                        Text("Установка будильника")
                    }

                    // Стандартная кнопка "Нет"
                    TextButton(onClick = { onResult(null,true,true) }) {
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