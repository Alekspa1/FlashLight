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
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.animation.core.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.generateDecayAnimationSpec
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenImage(uri: String, onDismiss: () -> Unit) {
    if (uri.isEmpty()) return

    val scope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }
    val offsetXAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(0f) }

    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }
    var intrinsicWidth by remember { mutableStateOf(0f) }
    var intrinsicHeight by remember { mutableStateOf(0f) }

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
        // Box(
        //     modifier = Modifier
        //         .fillMaxSize()
        //         .background(Color.Black)
        //         .onGloballyPositioned { coordinates ->
        //             containerWidth = coordinates.size.width.toFloat()
        //             containerHeight = coordinates.size.height.toFloat()
        //         }
        //         // ✅ ПЕРЕНЕСЛИ СЮДА: Теперь весь экран слушает жесты пальцев
        //         .pointerInput(Unit) {
        //             detectTransformGestures { _, pan, gestureZoom, _ ->
        //                 scope.launch {
        //                     val nextScale = (scaleAnim.value * gestureZoom).coerceIn(1f, 5f)
        //                     scaleAnim.snapTo(nextScale)

        //                     if (nextScale > 1f && intrinsicWidth > 0 && intrinsicHeight > 0) {
        //                         val sensitivity = 2.0f
        //                         val srcRatio = intrinsicWidth / intrinsicHeight
        //                         val dstRatio = containerWidth / containerHeight

        //                         val actualImageWidth = if (srcRatio > dstRatio) containerWidth else containerHeight * srcRatio
        //                         val actualImageHeight = if (srcRatio > dstRatio) containerWidth / srcRatio else containerHeight

        //                         val maxOffsetX = (actualImageWidth * (nextScale - 1f)) / 2f
        //                         val maxOffsetY = (actualImageHeight * (nextScale - 1f)) / 2f

        //                         offsetXAnim.snapTo((offsetXAnim.value + (pan.x * sensitivity)).coerceIn(-maxOffsetX, maxOffsetX))
        //                         offsetYAnim.snapTo((offsetYAnim.value + (pan.y * sensitivity)).coerceIn(-maxOffsetY, maxOffsetY))
        //                     } else {
        //                         offsetXAnim.snapTo(0f)
        //                         offsetYAnim.snapTo(0f)
        //                     }
        //                 }
        //             }
        //         }
        //         .combinedClickable(
        //             interactionSource = remember { MutableInteractionSource() },
        //             indication = null,
        //             onClick = {  },
        //             onDoubleClick = {
        //                 scope.launch {
        //                     if (scaleAnim.value > 1f) {
        //                         launch { scaleAnim.animateTo(1f, springSpec) }
        //                         launch { offsetXAnim.animateTo(0f, springSpec) }
        //                         launch { offsetYAnim.animateTo(0f, springSpec) }
        //                     } else {
        //                         launch { scaleAnim.animateTo(3f, springSpec) }
        //                     }
        //                 }
        //             }
        //         ),
        //     contentAlignment = Alignment.Center
        // ) 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onGloballyPositioned { coordinates ->
                    containerWidth = coordinates.size.width.toFloat()
                    containerHeight = coordinates.size.height.toFloat()
                }
                .pointerInput(Unit) {
                    // Создаем трекер скорости для эффекта скольжения
                    val velocityTracker = VelocityTracker()
                    
                    // Создаем спецификацию затухания (физика скольжения по льду)
                    val decaySpec = SplineBasedFloatDecayAnimationSpec(this).generateDecayAnimationSpec<Float>()

                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        scope.launch {
                            val nextScale = (scaleAnim.value * gestureZoom).coerceIn(1f, 5f)
                            scaleAnim.snapTo(nextScale)

                            if (nextScale > 1f && intrinsicWidth > 0 && intrinsicHeight > 0) {
                                val srcRatio = intrinsicWidth / intrinsicHeight
                                val dstRatio = containerWidth / containerHeight

                                val actualImageWidth = if (srcRatio > dstRatio) containerWidth else containerHeight * srcRatio
                                val actualImageHeight = if (srcRatio > dstRatio) containerWidth / srcRatio else containerHeight

                                val maxOffsetX = (actualImageWidth * (nextScale - 1f)) / 2f
                                val maxOffsetY = (actualImageHeight * (nextScale - 1f)) / 2f

                                // Считаем новые координаты пальца
                                val newX = (offsetXAnim.value + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                val newY = (offsetYAnim.value + pan.y).coerceIn(-maxOffsetY, maxOffsetY)

                                // Мгновенно двигаем за пальцем
                                offsetXAnim.snapTo(newX)
                                offsetYAnim.snapTo(newY)

                                // Записываем скорость движения, пока палец на экране
                                velocityTracker.addPosition(System.currentTimeMillis(), pan)
                            } else {
                                offsetXAnim.snapTo(0f)
                                offsetYAnim.snapTo(0f)
                            }
                        }
                    }
                    
                    // КОГДА ПОЛЬЗОВАТЕЛЬ ОТПУСТИЛ ПАЛЕЦ (эффект льда):
                    pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                // Если все пальцы подняты — запускаем скольжение
                                if (event.changes.all { !it.pressed }) {
                                    val velocity = velocityTracker.calculateVelocity()
                                    
                                    // Если скорость свайпа была достаточной — плавно катим картинку
                                    if (abs(velocity.x) > 100f || abs(velocity.y) > 100f) {
                                        scope.launch {
                                            // Рассчитываем границы, чтобы скольжение не вылетело за экран
                                            val srcRatio = intrinsicWidth / intrinsicHeight
                                            val dstRatio = containerWidth / containerHeight
                                            val actualImageWidth = if (srcRatio > dstRatio) containerWidth else containerHeight * srcRatio
                                            val actualImageHeight = if (srcRatio > dstRatio) containerWidth / srcRatio else containerHeight
                                            val maxOffsetX = (actualImageWidth * (scaleAnim.value - 1f)) / 2f
                                            val maxOffsetY = (actualImageHeight * (scaleAnim.value - 1f)) / 2f

                                            // Анимируем скольжение с затуханием скорости
                                            launch {
                                                offsetXAnim.animateDecay(velocity.x, decaySpec) {
                                                    // Удерживаем в границах экрана во время скольжения
                                                    if (value < -maxOffsetX || value > maxOffsetX) {
                                                        scope.launch { offsetXAnim.snapTo(value.coerceIn(-maxOffsetX, maxOffsetX)) }
                                                    }
                                                }
                                            }
                                            launch {
                                                offsetYAnim.animateDecay(velocity.y, decaySpec) {
                                                    if (value < -maxOffsetY || value > maxOffsetY) {
                                                        scope.launch { offsetYAnim.snapTo(value.coerceIn(-maxOffsetY, maxOffsetY)) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    velocityTracker.resetTracking() // Сбрасываем трекер для следующего раза
                                }
                            }
                        }
                    }
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { if (scaleAnim.value == 1f) onDismiss() },
                    onDoubleClick = {
                        scope.launch {
                            if (scaleAnim.value > 1f) {
                                launch { scaleAnim.animateTo(1f, springSpec) }
                                launch { offsetXAnim.animateTo(0f, springSpec) }
                                launch { offsetYAnim.animateTo(0f, springSpec) }
                            } else {
                                launch { scaleAnim.animateTo(3f, springSpec) }
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ){
            AsyncImage(
                model = uri,
                contentDescription = "Крупный план",
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value,
                        translationX = offsetXAnim.value,
                        translationY = offsetYAnim.value
                    ), // <-- ЗДЕСЬ pointerInput ПОЛНОСТЬЮ УБРАН
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    intrinsicWidth = state.painter.intrinsicSize.width
                    intrinsicHeight = state.painter.intrinsicSize.height
                }
            )

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
}
