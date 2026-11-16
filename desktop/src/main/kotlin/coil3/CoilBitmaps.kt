package coil3

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

/** Bridges android.graphics.Bitmap (what upstream code produces) to Coil's desktop Image. */
fun android.graphics.Bitmap.asImage(shareable: Boolean = true): Image = image.toComposeImageBitmap().asSkiaBitmap().asImage(shareable)
