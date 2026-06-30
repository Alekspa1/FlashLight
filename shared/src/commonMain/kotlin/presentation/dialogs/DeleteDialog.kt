package presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import data.room.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(onResult :(Boolean) -> Unit ={},){
    AlertDialog(onDismissRequest = { onResult(false) },
        title = {
            Text("Вы действительно хотите это удалить?")
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