package androidx.activity.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/** Window-level back stack: Esc and the mouse back button call [dispatch]; the newest enabled handler wins. */
object BackDispatcher {
  private val handlers = ArrayList<Pair<Any, () -> Boolean>>()
  fun register(token: Any, handler: () -> Boolean) { synchronized(handlers) { handlers.add(token to handler) } }
  fun unregister(token: Any) { synchronized(handlers) { handlers.removeAll { it.first === token } } }
  /** Returns true when something consumed the back action. */
  fun dispatch(): Boolean = synchronized(handlers) { handlers.toList() }.asReversed().any { it.second() }
}

@Composable
fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) {
  val current by rememberUpdatedState(onBack)
  val token = androidx.compose.runtime.remember { Any() }
  DisposableEffect(enabled) {
    if (enabled) BackDispatcher.register(token) { current(); true }
    onDispose { BackDispatcher.unregister(token) }
  }
}
