package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import kotlinx.coroutines.launch

/**
 * Desktop implementation of me.saket.swipe: drag a row sideways with the mouse to reveal and
 * trigger actions. Same public API as the Android library.
 */
@Stable
class SwipeAction(
    val onSwipe: () -> Unit,
    val icon: @Composable () -> Unit,
    val background: Color,
    val weight: Double = 1.0,
    val isUndo: Boolean = false,
) {
  constructor(icon: @Composable () -> Unit, background: Color, weight: Double = 1.0, isUndo: Boolean = false, onSwipe: () -> Unit) :
      this(onSwipe, icon, background, weight, isUndo)
}

@Stable
class SwipeableActionsState internal constructor() {
  internal val offsetPx = Animatable(0f)
  val offset: Float get() = offsetPx.value
}

@Composable fun rememberSwipeableActionsState(): SwipeableActionsState = remember { SwipeableActionsState() }

@Composable
fun SwipeableActionsBox(
    modifier: Modifier = Modifier,
    state: SwipeableActionsState = rememberSwipeableActionsState(),
    startActions: List<SwipeAction> = emptyList(),
    endActions: List<SwipeAction> = emptyList(),
    swipeThreshold: Dp = 40.dp,
    backgroundUntilSwipeThreshold: Color = Color.Transparent,
    content: @Composable () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val density = LocalDensity.current
  val thresholdPx = with(density) { swipeThreshold.toPx() }
  var widthPx = remember { floatArrayOf(0f) }
  val offset = state.offsetPx.value
  val active: SwipeAction? = when {
    offset > thresholdPx -> startActions.firstOrNull()
    offset < -thresholdPx -> endActions.firstOrNull()
    else -> null
  }
  Box(
      modifier.onSizeChanged { widthPx[0] = it.width.toFloat() }.draggable(
          orientation = Orientation.Horizontal,
          state = rememberDraggableState { delta ->
            val allowed = (state.offsetPx.value + delta).let { v ->
              when {
                v > 0 && startActions.isEmpty() -> 0f
                v < 0 && endActions.isEmpty() -> 0f
                else -> v.coerceIn(-widthPx[0], widthPx[0])
              }
            }
            scope.launch { state.offsetPx.snapTo(allowed) }
          },
          onDragStopped = {
            val a = active
            scope.launch {
              state.offsetPx.animateTo(0f)
              a?.onSwipe?.invoke()
            }
          },
      )
  ) {
    Box(Modifier.matchParentSizeCompat().background(active?.background ?: backgroundUntilSwipeThreshold)) {
      if (offset > 0) startActions.firstOrNull()?.let { Box(Modifier.fillMaxHeight().align(Alignment.CenterStart), contentAlignment = Alignment.CenterStart) { it.icon() } }
      if (offset < 0) endActions.firstOrNull()?.let { Box(Modifier.fillMaxHeight().align(Alignment.CenterEnd), contentAlignment = Alignment.CenterEnd) { it.icon() } }
    }
    Box(Modifier.offset { IntOffset(offset.toInt(), 0) }) { content() }
  }
}

private fun Modifier.matchParentSizeCompat(): Modifier = this.fillMaxSize()
