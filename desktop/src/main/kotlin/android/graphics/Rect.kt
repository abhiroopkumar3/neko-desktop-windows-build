package android.graphics

data class Rect(var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0) {
  fun width() = right - left
  fun height() = bottom - top
  fun centerX() = (left + right) / 2
  fun centerY() = (top + bottom) / 2
  fun isEmpty() = left >= right || top >= bottom
  fun set(l: Int, t: Int, r: Int, b: Int) { left = l; top = t; right = r; bottom = b }
}
