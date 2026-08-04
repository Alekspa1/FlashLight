package presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import data.repostitory.AndroidSoundPlayer
import org.koin.compose.koinInject

@Composable
actual fun DialogSoundAndroid(
    selectUri: String,
    listSound: Map<String, String>,
    onClick: (String) -> Unit,
    onCancel: () -> Unit
) {
    // В состоянии храним именно URI, чтобы не было путаницы с одинаковыми названиями
    var selectedUri by remember { mutableStateOf(selectUri) }
    val soundPlayer = koinInject<AndroidSoundPlayer>()

    // Автоматически выключит звук, если диалог закроют кнопкой "Назад" или смахнут
    DisposableEffect(Unit) {
        onDispose { soundPlayer.stop() }
    }

    AlertDialog(
        onDismissRequest = {
            soundPlayer.stop()
            onCancel()
        },
        title = { Text("Выберите звук") },
        confirmButton = {
            TextButton(onClick = {
                soundPlayer.stop()
                onClick(selectedUri) // Отдаем выбранный URI для сохранения локально
            }) {
                Text("Ок")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                soundPlayer.stop()
                onCancel()
            }) {
                Text("Отмена")
            }
        },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Итерируемся по мапе: name — это ключ (название), uriStr — значение (путь)
                listSound.forEach { (name, uriStr) ->
                    val isSelected = (uriStr == selectedUri)

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    selectedUri = uriStr // Сохраняем URI кликнутого трека
                                    soundPlayer.playSound(uriStr.toUri()) // Играем его по URI
                                }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null // null переносит клик на весь Row
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Пользователь видит НАЗВАНИЕ песни
                        Text(text = name, fontSize = 18.sp)
                    }
                }
            }
        }
    )
}