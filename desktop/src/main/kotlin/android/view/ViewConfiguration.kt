package android.view

import android.content.Context

class ViewConfiguration private constructor() {
  val scaledTouchSlop: Int = 8
  val scaledDoubleTapSlop: Int = 100
  val scaledMinimumFlingVelocity: Int = 50
  val scaledMaximumFlingVelocity: Int = 8000
  companion object {
    @JvmStatic fun get(context: Context) = ViewConfiguration()
    @JvmStatic fun getLongPressTimeout() = 500
    @JvmStatic fun getDoubleTapTimeout() = 300
    @JvmStatic fun getTapTimeout() = 100
  }
}
