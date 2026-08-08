package presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    theme: Theme = ThemeNeon(),
    onResult :(Boolean) -> Unit ={},

){
    AlertDialog(onDismissRequest = { onResult(false) },
        title = {
            Text("Вы действительно хотите это удалить?", color = theme.textColor)
        },
        confirmButton = {
            TextButton(onClick = {onResult(true)}){
                Text("Да")
            }
        },
        dismissButton = {
            TextButton(onClick = {onResult(false)}){
                Text("Нет")
            }
        }
        )
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteDialog(){
    DeleteDialog()
}