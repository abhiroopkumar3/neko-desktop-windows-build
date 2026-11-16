package jp.wasabeef.gap

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Desktop implementation of jp.wasabeef.gap.Gap: spacing along the parent's main axis. The Row and
 * Column overloads are picked by the implicit receiver, like the Android library's behavior.
 */
@Composable fun RowScope.Gap(size: Dp) = Spacer(Modifier.width(size))

@Composable fun ColumnScope.Gap(size: Dp) = Spacer(Modifier.height(size))

/** Outside a Row or Column there is no main axis; fall back to a square gap. */
@Composable fun Gap(size: Dp) = Spacer(Modifier.size(size))
