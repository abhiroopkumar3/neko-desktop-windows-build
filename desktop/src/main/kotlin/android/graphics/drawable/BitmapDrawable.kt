package android.graphics.drawable

import android.graphics.Bitmap

class BitmapDrawable(val bitmap: Bitmap) : Drawable() {
  constructor(res: android.content.res.Resources?, bitmap: Bitmap) : this(bitmap)
  override val intrinsicWidth: Int get() = bitmap.width
  override val intrinsicHeight: Int get() = bitmap.height
}
