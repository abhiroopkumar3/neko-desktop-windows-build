package android.graphics

data class PointF(@JvmField var x: Float = 0f, @JvmField var y: Float = 0f) {
  fun set(nx: Float, ny: Float) { x = nx; y = ny }
}
