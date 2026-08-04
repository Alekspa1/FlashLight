package presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
    var selectedUri by remember { mutableStateOf(selectUri) }
    val soundPlayer = koinInject<AndroidSoundPlayer>()

    // Превращаем мапу в список пар, чтобы работать с индексами
    val soundList = remember(listSound) { listSound.toList() }

    // Находим индекс текущей выбранной мелодии в списке
    val initialIndex = remember(soundList, selectUri) {
        val index = soundList.indexOfFirst { it.second == selectUri }
        if (index != -1) index else 0
    }

    // Состояние скролла для LazyColumn. Передаем initialIndex,
    // чтобы список ИЗНАЧАЛЬНО отрисовался прокрученным на нужную позицию
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

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
                onClick(selectedUri)
            }) { Text("Ок") }
        },
        dismissButton = {
            TextButton(onClick = {
                soundPlayer.stop()
                onCancel()
            }) { Text("Отмена") }
        },
        text = {
            // Используем LazyColumn вместо Column для поддержки авто-скролла и оптимизации памяти
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(soundList) { _, (name, uriStr) ->
                    val isSelected = (uriStr == selectedUri)

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    selectedUri = uriStr
                                    soundPlayer.playSound(uriStr.toUri())
                                }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = name, fontSize = 18.sp)
                    }
                }
            }
        }
    )
}