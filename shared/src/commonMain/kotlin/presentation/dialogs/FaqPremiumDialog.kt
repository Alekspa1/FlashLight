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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


@Composable
fun FaqPremiumDialog(
    text: String = "Тест",
    image: DrawableResource? = null, // Поставил null по умолчанию для безопасности, замени на свой Res
    onClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = { onClick() }) {
        // Card задает форму и белый (или кастомный) фон нашему окошку
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Отступ самого диалога от краев экрана
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White) 
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Отступы для текста, чтобы он не прижимался к краям карточки
                Text(
                    text = text, 
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                )
                
                if (image != null) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth // Картинка займет всю ширину карточки «в край»
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun FaqPremiumDialogPrev(){
    FaqPremiumDialog {  }
}
