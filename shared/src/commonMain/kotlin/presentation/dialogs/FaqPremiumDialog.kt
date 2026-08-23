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
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip

@Composable
fun FaqPremiumDialog(
    text: String = "Тест",
    image: DrawableResource? = null,
    theme: Theme = ThemeNeon(),
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        // Surface выступает контейнером и автоматически применяет цвет фона темы
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Отступ самого окна от краев экрана
            shape = RoundedCornerShape(28.dp), // Стандартное скругление диалогов Material 3
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Текст сообщения с правильными отступами
                Text(
                    text = text, 
                    color = theme.textColor,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(
                        top = 24.dp, 
                        bottom = if (image != null) 16.dp else 24.dp, 
                        start = 24.dp, 
                        end = 24.dp
                    )
                )
                
                // Если картинка передана, она аккуратно встает в край нижней части
                if (image != null) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)), // Скругляем низ картинки по форме Surface
                        contentScale = ContentScale.FillWidth
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
