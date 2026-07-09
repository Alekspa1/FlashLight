package presentation.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenImage(uri: String, onDismiss: () -> Unit) {
    if (uri.isEmpty()) return

    // Скоуп для запуска анимаций в ответ на жесты
    val scope = rememberCoroutineScope()

    // Аниматоры для плавных переходов и мгновенных привязок (snap)
    val scaleAnim = remember { Animatable(1f) }
    val offsetXAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(0f) }

    // Размеры контейнера и картинки для расчетов ограничений экрана
    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }
    var intrinsicWidth by remember { mutableStateOf(0f) }
    var intrinsicHeight by remember { mutableStateOf(0f) }

    // Настройка пружины для двойного клика (с высокой жесткостью, чтобы сброс был четким)
    val springSpec = remember {
        spring<Float>(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioNoBouncy
        )
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onGloballyPositioned { coordinates ->
                    containerWidth = coordinates.size.width.toFloat()
                    containerHeight = coordinates.size.height.toFloat()
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Можно закрывать по клику на фон при scale == 1 */ },
                    onDoubleClick = {
                        scope.launch {
                            if (scaleAnim.value > 1f) {
                                // Плавно сбрасываем всё к исходному состоянию
                                launch { scaleAnim.animateTo(1f, springSpec) }
                                launch { offsetXAnim.animateTo(0f, springSpec) }
                                launch { offsetYAnim.animateTo(0f, springSpec) }
                            } else {
                                // Плавно увеличиваем в 3 раза по центру
                                launch { scaleAnim.animateTo(3f, springSpec) }
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = uri,
                contentDescription = "Крупный план",
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value,
                        translationX = offsetXAnim.value,
                        translationY = offsetYAnim.value
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            scope.launch {
                                // Расчет нового масштаба
                                val nextScale = (scaleAnim.value * gestureZoom).coerceIn(1f, 5f)
                                scaleAnim.snapTo(nextScale) // Мгновенно меняем без "киселя"

                                if (nextScale > 1f && intrinsicWidth > 0 && intrinsicHeight > 0) {
                                    val sensitivity = 2.0f
                                    val srcRatio = intrinsicWidth / intrinsicHeight
                                    val dstRatio = containerWidth / containerHeight

                                    val actualImageWidth = if (srcRatio > dstRatio) containerWidth else containerHeight * srcRatio
                                    val actualImageHeight = if (srcRatio > dstRatio) containerWidth / srcRatio else containerHeight

                                    // Границы, за которые картинка не должна улетать
                                    val maxOffsetX = (actualImageWidth * (nextScale - 1f)) / 2f
                                    val maxOffsetY = (actualImageHeight * (nextScale - 1f)) / 2f

                                    // Мгновенно двигаем за пальцем строго в границах
                                    offsetXAnim.snapTo((offsetXAnim.value + (pan.x * sensitivity)).coerceIn(-maxOffsetX, maxOffsetX))
                                    offsetYAnim.snapTo((offsetYAnim.value + (pan.y * sensitivity)).coerceIn(-maxOffsetY, maxOffsetY))
                                } else {
                                    // Если картинка вернулась к исходному размеру, центрируем её обратно
                                    offsetXAnim.snapTo(0f)
                                    offsetYAnim.snapTo(0f)
                                }
                            }
                        }
                    },
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    intrinsicWidth = state.painter.intrinsicSize.width
                    intrinsicHeight = state.painter.intrinsicSize.height
                }
            )

            // Кнопка закрытия
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
            }
        }
    }
}