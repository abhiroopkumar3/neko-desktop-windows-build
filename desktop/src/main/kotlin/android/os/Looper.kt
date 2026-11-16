package android.os

import javax.swing.SwingUtilities

/** The "main" looper is the Swing event thread that Compose Desktop renders on. */
class Looper private constructor(val isMain: Boolean) {
  val thread: Thread get() = Thread.currentThread()
  companion object {
    private val main = Looper(true)
    @JvmStatic fun getMainLooper(): Looper = main
    @JvmStatic fun myLooper(): Looper? = if (SwingUtilities.isEventDispatchThread()) main else null
  }
}

open class Handler(val looper: Looper = Looper.getMainLooper()) {
  constructor(looper: Looper, callback: Any?) : this(looper)
  fun post(r: Runnable): Boolean { javax.swing.SwingUtilities.invokeLater(r); return true }
  fun post(r: () -> Unit): Boolean = post(Runnable { r() })
  fun postDelayed(r: Runnable, delayMs: Long): Boolean {
    javax.swing.Timer(delayMs.toInt()) { r.run() }.apply { isRepeats = false; start() }
    return true
  }
  fun postDelayed(r: () -> Unit, delayMs: Long): Boolean = postDelayed(Runnable { r() }, delayMs)
  fun removeCallbacks(r: Runnable) {}
}
