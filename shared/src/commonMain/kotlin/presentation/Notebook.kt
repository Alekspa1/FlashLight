package presentation

import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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



@Composable

fun Notebook(viewModel: MainViewModel){

    val lifecycleOwner = LocalLifecycleOwner.current

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

    NoteBookContent(viewModel.stateTextNotebook){newtext-> viewModel.stateTextNotebook = newtext}

}

    @Composable
    fun NoteBookContent(text: String, onTextChange : (String) -> Unit = {}){
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
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
                    Image(
                        painter = painterResource(Res.drawable.ic_micro_neon),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp) ,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_del_notebook_neon),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(50.dp)
                            .clickable{onTextChange("")},
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    }

@Preview(showBackground = true)
@Composable
fun Test(){
    NoteBookContent("NoteBookContent")

}
