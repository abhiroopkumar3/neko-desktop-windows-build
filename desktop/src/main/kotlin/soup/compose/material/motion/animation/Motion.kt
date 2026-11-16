package soup.compose.material.motion.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** Slide distance of the Material shared-axis transition (30dp as in the Material motion spec). */
@Composable fun rememberSlideDistance(): Int = with(LocalDensity.current) { remember(this) { 30.dp.roundToPx() } }

/** Forward/backward shared axis on the X axis, built from stock Compose transitions. */
fun <S> AnimatedContentTransitionScope<S>.materialSharedAxisX(forward: Boolean, slideDistance: Int): ContentTransform {
  val dir = if (forward) 1 else -1
  return (slideInHorizontally(tween(300)) { dir * slideDistance } + fadeIn(tween(210, 90))) togetherWith
      (slideOutHorizontally(tween(300)) { -dir * slideDistance } + fadeOut(tween(90)))
}
