package presentation.screens

import CommonConst
import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
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
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.imageResource
import presentation.theme.ThemeZabor


@Composable

fun Notebook(viewModel: MainViewModel,pageIndex: Int){

    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    if(pageIndex != 0) focusManager.clearFocus()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.saveText() // ЖЕЛЕЗНО сохраняем данные на диск!
            }
            
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewModel.saveText()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    NoteBookContent(
        text = viewModel.stateTextNotebook,
        showDialog = viewModel.showDialog,
       // onResultDialog = {dialog-> viewModel.showDialog = dialog},
        onTextChange = {newtext->
            viewModel.stateTextNotebook = newtext},
        theme = viewModel.themeState,
        size = viewModel.sizeState
        )


}

    @Composable
    fun NoteBookContent(
        text: String,
        showDialog : DialogState = DialogState(),
       // onResultDialog : (DialogState) -> Unit = {},
        onTextChange : (String) -> Unit = {},
        theme: Theme = ThemeNeon(),
        size: Size = SizeNormal()
        )
    {
            var openDialog by remember { mutableStateOf(false) }
            // if(showDialog.isWho == CommonConst.DELETE_DIALOG_ITEM){
            // DeleteDialog(theme = theme) {result->
            // if(result) onTextChange("")
            //  onResultDialog(DialogState())
            // }
            // }

            if(openDialog) {
            DeleteDialog(theme = theme) {result->
             if(result) onTextChange("")
              openDialog = false
             }    
            }

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    then(
                        if (theme == ThemeNeon()) Modifier
                            .border(width = 3.dp,
                                color = theme.noteBookBorder,
                                shape = RoundedCornerShape(10.dp))
                    else Modifier),
                value = text,

                onValueChange = { newText ->
                    onTextChange( newText)
                },
                shape = RoundedCornerShape(10.dp), // Выставляем ваши 10.dp скругления из XML shape

                // НАСТРАИВАЕМ ЦВЕТА И ВАШУ НЕОНОВУЮ СТИЛИСТИКУ:
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.textColor,
                    unfocusedTextColor = theme.textColor,

                    // 1. ТВОЙ БИРЮЗОВЫЙ НЕОН НА ФОНЕ (Solid):
                    focusedContainerColor = theme.noteBookBackground,
                    unfocusedContainerColor = theme.noteBookBackground,

                    // 2. ТВОЙ СИНЕ-ГОЛУБОЙ НЕОН НА ОБВОДКЕ (Stroke):
                   // focusedBorderColor = Color(0x9900E2FF),
                   // unfocusedBorderColor = Color(0x9900E2FF),

                    // Курсор делаем сочным сине-голубым:
                    cursorColor = theme.textColor
                )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp)){
                    IconButton(modifier = Modifier.size(50.dp).align(Alignment.Center),
                        onClick = { },
                    ) {
                        Image(
                            painter = theme.iconMicro(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()


                            )
                    }
                    IconButton(modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
                        onClick = {openDialog = true},
                    ){
                        Image(
                            painter = theme.iconDel(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
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
    NoteBookContent("NoteBookContent", theme = ThemeNeon())

}
