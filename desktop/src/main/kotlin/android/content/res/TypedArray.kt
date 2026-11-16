package android.content.res

import android.graphics.drawable.Drawable

/** Theme attribute lookups from View-era code get neutral light-theme defaults; Compose themes drive real colors. */
class TypedArray(private val attrs: IntArray) : AutoCloseable {
  fun getColor(index: Int, default: Int): Int = default.takeIf { it != 0 } ?: 0xFF1C1B1F.toInt()
  fun getDrawable(index: Int): Drawable? = null
  fun getResourceId(index: Int, default: Int): Int = default
  fun getDimensionPixelSize(index: Int, default: Int): Int = default
  fun getBoolean(index: Int, default: Boolean): Boolean = default
  fun recycle() {}
  override fun close() = recycle()
}
