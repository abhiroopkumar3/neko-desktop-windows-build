package com.google.accompanist.drawablepainter

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.graphics.drawable.toBitmap

@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter = remember(drawable) {
  val bmp = drawable?.toBitmap()
  if (bmp == null) ColorPainter(Color.Transparent) else BitmapPainter(bmp.image.toComposeBitmap())
}

private fun java.awt.image.BufferedImage.toComposeBitmap() = this.toComposeImageBitmap()
