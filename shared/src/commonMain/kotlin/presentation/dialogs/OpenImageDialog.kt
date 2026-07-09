package presentation.dialogs

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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenImage(uri: String, onDismiss: () -> Unit) {
    if (uri.isEmpty()) return

    // 1. Создаем БАЗОВЫЕ состояния (целевые значения для анимации)
    var targetScale by remember { mutableStateOf(1f) }
    var targetOffsetX by remember { mutableStateOf(0f) }
    var targetOffsetY by remember { mutableStateOf(0f) }

    // Размеры контейнера и картинки для расчетов
    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }
    var intrinsicWidth by remember { mutableStateOf(0f) }
    var intrinsicHeight by remember { mutableStateOf(0f) }

    // 2. Оборачиваем их в анимацию (spring дает красивый естественный эффект без рывков)
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = targetOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

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
                    onClick = { if (targetScale == 1f) onDismiss() },
                    onDoubleClick = {
                        // 3. ПЛАВНЫЙ СБРОС ИЛИ УВЕЛИЧЕНИЕ
                        if (targetScale > 1f) {
                            targetScale = 1f
                            targetOffsetX = 0f
                            targetOffsetY = 0f
                        } else {
                            targetScale = 3f
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = uri,
                contentDescription = "Крупный план",
                modifier = Modifier
                    // 4. Применяем АНИМИРОВАННЫЕ значения к слою отрисовки
                    .graphicsLayer(
                        scaleX = animatedScale,
                        scaleY = animatedScale,
                        translationX = animatedOffsetX,
                        translationY = animatedOffsetY
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            // Изменяем целевое значение масштаба пальцами
                            targetScale = (targetScale * gestureZoom).coerceIn(1f, 5f)

                            if (targetScale > 1f && intrinsicWidth > 0 && intrinsicHeight > 0) {
                                val srcRatio = intrinsicWidth / intrinsicHeight
                                val dstRatio = containerWidth / containerHeight

                                val actualImageWidth = if (srcRatio > dstRatio) containerWidth else containerHeight * srcRatio
                                val actualImageHeight = if (srcRatio > dstRatio) containerWidth / srcRatio else containerHeight

                                val maxOffsetX = (actualImageWidth * (targetScale - 1f)) / 2f
                                val maxOffsetY = (actualImageHeight * (targetScale - 1f)) / 2f

                                // Изменяем целевые координаты смещения пальцами
                                targetOffsetX = (targetOffsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                targetOffsetY = (targetOffsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                            } else {
                                targetOffsetX = 0f
                                targetOffsetY = 0f
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