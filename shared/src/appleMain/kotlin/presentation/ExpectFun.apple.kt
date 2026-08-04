package presentation

import androidx.compose.runtime.Composable

@Composable
actual fun DialogSoundAndroid(
    selectUri: String,
    listSound: Map<String, String>,
    onClick: (String) -> Unit,
    onCancel: () -> Unit
) {
}