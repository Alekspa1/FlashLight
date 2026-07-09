package presentation.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.room.ListCategory

@Composable
fun AddOrChangeCategoryDialog(category: ListCategory? = null,
                              onSave : (ListCategory?,String) -> Unit = {_,_->},
                              onCancel : ()-> Unit = {},){

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        withFrameMillis { }  // Ждем, пока отрендерится первый кадр окна
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    var stateTextName by remember { mutableStateOf(category?.name ?: "") }

        AlertDialog(
            onDismissRequest = { onCancel()},
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth() // Настройка ширины применится всегда
                        .then(
                            if (category == null) Modifier.focusRequester(focusRequester) else Modifier
                        ),
                    value = stateTextName,
                    onValueChange = { newText ->
                        stateTextName = newText
                    },
                    shape = RoundedCornerShape(10.dp),
                    label = {Text(text = "Название категории", color = Color.Gray)},

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name =
                            if (stateTextName.isEmpty()) ("Без названия")
                            else stateTextName.trim()
                        onSave(category,name)},
                    content = { Text("Ок") })
            },
            dismissButton = {
                TextButton(
                    onClick = {onCancel()},
                    content = { Text("Отмена") })

            }
        )


}


@Preview(showBackground = true)
@Composable
fun Prewiew(){
    AddOrChangeCategoryDialog()
}