package presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState, 
    onMove: (Int, Int) -> Unit,
    onDragEnd: () -> Unit // 👈 1. Передаем событие окончания драга наружу
): DragDropState {
    val scope = rememberCoroutineScope()
    val state = remember(lazyListState) {
        DragDropState(state = lazyListState, onMove = onMove, onDragEnd = onDragEnd, scope = scope)
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scroll {
                scrollBy(diff)
            }
        }
    }
    return state
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset = offset)
            },
            onDragStart = { offset -> dragDropState.onDragStart(offset) },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() },
        )
    }
}

class DragDropState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit,
    private val onDragEnd: () -> Unit, // 👈 Сохраняем колбэк окончания
) {
    // 👈 2. ТРЕКАЕМ ПО КЛЮЧУ (Any), А НЕ ПО ИНДЕКСУ (Int)
    var draggingItemKey by mutableStateOf<Any?>(null)
        private set

    internal val scrollChannel = Channel<Float>()
    private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)

    // Теперь индекс зажатого элемента динамически находится в списке по его ключу
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingItemKey }

    internal var previousTrackedKey by mutableStateOf<Any?>(null)
        private set

    internal var previousItemOffset = Animatable(0f)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                if (it.index > 0) { // Игнорируем заголовок категории (у него индекс 0)
                    draggingItemKey = it.key // Запоминаем уникальный ID
                    draggingItemInitialOffset = it.offset
                }
            }
    }

    internal fun onDragInterrupted() {
        if (draggingItemKey != null) {
            previousTrackedKey = draggingItemKey
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f),
                )
                previousTrackedKey = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemKey = null
        draggingItemInitialOffset = 0
        
        onDragEnd() // 👈 3. ВЫЗЫВАЕМ СОБЫТИЕ ЗАПИСИ В БД (Палец отпущен!)
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset.y
        val draggingItem = draggingItemLayoutInfo ?: return
        
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val middleOffset = startOffset + (endOffset - startOffset) / 2f

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.toInt() in item.offset..(item.offset + item.size) &&
                    draggingItem.index != item.index && item.index > 0
        }

        if (targetItem != null) {
            val isMovingDown = targetItem.index > draggingItem.index
            
            // 👈 4. Смягчаем порог пересечения для элементов разной высоты (40% и 60%)
            val isValidMove = if (isMovingDown) {
                middleOffset > targetItem.offset + (targetItem.size * 0.4f)
            } else {
                middleOffset < targetItem.offset + (targetItem.size * 0.6f)
            }

            if (isValidMove) {
                if (draggingItem.index == state.firstVisibleItemIndex ||
                    targetItem.index == state.firstVisibleItemIndex
                ) {
                    state.requestScrollToItem(
                        state.firstVisibleItemIndex,
                        state.firstVisibleItemScrollOffset,
                    )
                }
                onMove.invoke(draggingItem.index, targetItem.index)
                // Ключ зажатого элемента остался прежним. В следующем кадре рекомпозиции 
                // draggingItemLayoutInfo сам найдет карточку по её новому индексу.
            }
        } else {
            val overscroll = when {
                draggingItemDraggedDelta > 0 -> (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                draggingItemDraggedDelta < 0 -> (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)
                else -> 0f
            }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }
}

@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    key: Any, // 👈 Принимаем key (item.id) вместо индекса
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean) -> Unit,
) {
    val dragging = key == dragDropState.draggingItemKey
    val draggingModifier =
        if (dragging) {
            Modifier.zIndex(1f).graphicsLayer { translationY = dragDropState.draggingItemOffset }
        } else if (key == dragDropState.previousTrackedKey) {
            Modifier.zIndex(1f).graphicsLayer {
                translationY = dragDropState.previousItemOffset.value
            }
        } else {
            Modifier.animateItem()
        }
    Column(modifier = modifier.then(draggingModifier)) { content(dragging) }
}
