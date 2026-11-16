package androidx.navigation3.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/** The desktop build keeps the back stack in memory (no process-death restore), so no serializer configuration is needed. */
@Composable
fun rememberNavBackStack(vararg elements: NavKey): NavBackStack<NavKey> = remember { NavBackStack<NavKey>(*elements) }
