package androidx.core.graphics.drawable

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable.toBitmap(width: Int = intrinsicWidth.coerceAtLeast(1), height: Int = intrinsicHeight.coerceAtLeast(1)): Bitmap =
    (this as? BitmapDrawable)?.bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
