package androidx.compose.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner

/** The old location of the lifecycle owner local that upstream still imports. */
object LocalLifecycleOwner {
  val current: LifecycleOwner @Composable get() = androidx.lifecycle.compose.LocalLifecycleOwner.current
}

val LocalResources: ProvidableCompositionLocal<android.content.res.Resources> = staticCompositionLocalOf { android.content.DesktopContext.resources }

