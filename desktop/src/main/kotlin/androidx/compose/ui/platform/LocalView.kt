package androidx.compose.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf
import android.view.View

val LocalView = staticCompositionLocalOf { View() }
