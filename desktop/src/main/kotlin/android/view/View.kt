package android.view

import android.content.Context

/** View-era base class; only here so type references from shared code resolve. */
open class View(val viewContext: Context? = null) {
  open fun performHapticFeedback(type: Int): Boolean = false
  open fun performHapticFeedback(type: Int, flags: Int): Boolean = false
  open fun setLayerType(type: Int, paint: Any?) {}
  companion object { const val VISIBLE = 0; const val INVISIBLE = 4; const val GONE = 8; const val LAYER_TYPE_SOFTWARE = 1; const val LAYER_TYPE_HARDWARE = 2; const val LAYER_TYPE_NONE = 0; const val LAYOUT_DIRECTION_LTR = 0 }
}

class Window {
  fun setFlags(flags: Int, mask: Int) {}
  fun clearFlags(flags: Int) {}
  fun addFlags(flags: Int) {}
}

class WindowMetrics(val bounds: android.graphics.Rect)

class WindowManager {
  val currentWindowMetrics: WindowMetrics
    get() = java.awt.Toolkit.getDefaultToolkit().screenSize.let { WindowMetrics(android.graphics.Rect(0, 0, it.width, it.height)) }
  class LayoutParams { companion object { const val FLAG_SECURE = 0x2000 } }
}


object HapticFeedbackConstants {
  const val LONG_PRESS = 0; const val VIRTUAL_KEY = 1; const val KEYBOARD_TAP = 3; const val CLOCK_TICK = 4
  const val CONTEXT_CLICK = 6; const val TEXT_HANDLE_MOVE = 9; const val CONFIRM = 16; const val REJECT = 17
  const val FLAG_IGNORE_GLOBAL_SETTING = 2
}
