package soup.compose.material.motion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MaterialFade(visible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  AnimatedVisibility(visible, modifier, enter = fadeIn(tween(150)) + scaleIn(tween(150), initialScale = 0.8f), exit = fadeOut(tween(75)) + scaleOut(tween(75), targetScale = 0.8f)) { content() }
}
