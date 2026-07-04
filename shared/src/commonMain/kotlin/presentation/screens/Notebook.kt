package presentation.screens

import CommonConst
import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.ic_del_notebook_neon
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.painterResource
import presentation.dialogs.DeleteDialog
import presentation.dialogs.DialogState
import androidx.compose.ui.platform.LocalFocusManager

@Composable

fun Notebook(viewModel: MainViewModel){

    val lifecycleOwner = LocalLifecycleOwner.current
     val focusManager = LocalFocusManager.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.saveText() // ЖЕЛЕЗНО сохраняем данные на диск!
                focusManager.clearFocus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewModel.saveText()
            lifecycleOwner.lifecycle.removeObserver(observer)
            focusManager.clearFocus()
        }
    }

    NoteBookContent(
        text = viewModel.stateTextNotebook,
        showDialog = viewModel.showDialog,
        onResultDialog = {dialog-> viewModel.showDialog = dialog},
        onTextChange = {newtext->
            viewModel.stateTextNotebook = newtext}
        )


}

    @Composable
    fun NoteBookContent(
        text: String,
        showDialog : DialogState = DialogState(),
        onResultDialog : (DialogState) -> Unit = {},
        onTextChange : (String) -> Unit = {},
        )
    {

            if(showDialog.isWho == CommonConst.DELETE_DIALOG){
            DeleteDialog {result->
            if(result) onTextChange("")
             onResultDialog(DialogState())
            }
            }

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .border(width = 3.dp, color = Color(0x9900E2FF), shape = RoundedCornerShape(10.dp)),
                value = text,
                onValueChange = { newText ->
                    onTextChange( newText)
                },
                shape = RoundedCornerShape(10.dp), // Выставляем ваши 10.dp скругления из XML shape

                // НАСТРАИВАЕМ ЦВЕТА И ВАШУ НЕОНОВУЮ СТИЛИСТИКУ:
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    // 1. ТВОЙ БИРЮЗОВЫЙ НЕОН НА ФОНЕ (Solid):
                    focusedContainerColor = Color(0x9900BCD4),
                    unfocusedContainerColor = Color(0x9900BCD4),

                    // 2. ТВОЙ СИНЕ-ГОЛУБОЙ НЕОН НА ОБВОДКЕ (Stroke):
                    focusedBorderColor = Color(0x9900E2FF),
                    unfocusedBorderColor = Color(0x9900E2FF),

                    // Курсор делаем сочным сине-голубым:
                    cursorColor = Color(0xFF00E2FF)
                )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp)){
                    IconButton(modifier = Modifier.align(Alignment.Center),
                        onClick = { },
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_micro_neon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)

                            )
                    }
                    IconButton(modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {onResultDialog(DialogState(CommonConst.DELETE_DIALOG))  },
                    ){
                        Image(
                            painter = painterResource(Res.drawable.ic_del_notebook_neon),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop

                        )
                    }
                }

            }
        }
    }

@Preview(showBackground = true)
@Composable
fun Test(){
    NoteBookContent("NoteBookContent")

}
