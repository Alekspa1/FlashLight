package presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun YandexBannerAd(adUnitId: String, modifier: Modifier = Modifier)

@Composable
expect fun DialogSoundAndroid( selectUri: String, // Передаем Uri текущего выбранного звука (строкой)
                               listSound: Map<String, String>, // Название -> Uri (строка)
                               onClick: (String) -> Unit = {}, // Возвращает Uri выбранного звука
                               onCancel: () -> Unit = {})