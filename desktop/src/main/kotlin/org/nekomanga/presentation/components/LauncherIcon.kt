package org.nekomanga.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import org.nekomanga.R

/** Desktop version: the launcher icon is drawn straight from the bundled mipmap (no adaptive-icon masking). */
@Composable
fun LauncherIcon(size: Dp) {
  Image(painter = painterResource(R.mipmap.ic_launcher), contentDescription = null, modifier = Modifier.requiredSize(size))
}

@Composable
fun adaptiveIconPainterResource(id: Int): Painter = painterResource(id)
