package android.view

/** Mouse and touch input arrive as Compose pointer events on desktop; this is the shared constants/holder. */
class MotionEvent(val x: Float = 0f, val y: Float = 0f, val action: Int = ACTION_DOWN) {
  companion object {
    const val ACTION_DOWN = 0; const val ACTION_UP = 1; const val ACTION_MOVE = 2; const val ACTION_CANCEL = 3
  }
}
