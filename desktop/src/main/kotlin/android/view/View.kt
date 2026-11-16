package android.view

import android.content.Context

/** View-era base class; only here so type references from shared code resolve. */
open class View(val viewContext: Context? = null) {
  companion object { const val VISIBLE = 0; const val INVISIBLE = 4; const val GONE = 8 }
}

class Window {
  fun setFlags(flags: Int, mask: Int) {}
  fun clearFlags(flags: Int) {}
  fun addFlags(flags: Int) {}
}

object WindowManager {
  class LayoutParams { companion object { const val FLAG_SECURE = 0x2000 } }
}
