package presentation.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_dialog_neon
import flashlight.shared.generated.resources.ic_alarm_faq
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun FaqPremiumDialog(text: String = "Тест",
                     image: DrawableResource? = Res.drawable.background_dialog_neon,
                     onClick : () -> Unit = {}){

    AlertDialog(onDismissRequest = { onClick() },
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(verticalArrangement = Arrangement.Center) {
                Text( text = text, fontSize = 16.sp)
                if(image != null){
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }


            }
        }
        )

}
@Preview
@Composable
fun FaqPremiumDialogPrev(){
    FaqPremiumDialog {  }
}
