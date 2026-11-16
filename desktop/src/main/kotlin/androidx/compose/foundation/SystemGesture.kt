package androidx.compose.foundation

import androidx.compose.ui.Modifier

/** Android edge-gesture exclusion zones do not exist on desktop. */
fun Modifier.systemGestureExclusion(): Modifier = this
