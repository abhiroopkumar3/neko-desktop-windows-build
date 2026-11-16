package androidx.compose.ui.platform

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.staticCompositionLocalOf
import android.content.DesktopContext

/** Desktop stand-ins for the Android-only composition locals upstream reads. */
val LocalContext = staticCompositionLocalOf<Context> { DesktopContext }

val LocalConfiguration = staticCompositionLocalOf { Configuration() }
