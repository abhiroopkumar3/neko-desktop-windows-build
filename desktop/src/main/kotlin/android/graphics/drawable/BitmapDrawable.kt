package android.graphics.drawable

import android.graphics.Bitmap

class BitmapDrawable(val bitmap: Bitmap) : Drawable() {
  override val intrinsicWidth: Int get() = bitmap.width
  override val intrinsicHeight: Int get() = bitmap.height
}
