package android.widget

import android.content.Context
import org.nekomanga.desktop.platform.Toasts

class Toast private constructor(private val text: CharSequence, private var duration: Int) {
  fun show() = Toasts.show(text.toString())
  fun cancel() {}
  fun setDuration(d: Int) { duration = d }

  companion object {
    const val LENGTH_SHORT = 0
    const val LENGTH_LONG = 1

    @JvmStatic fun makeText(context: Context, text: CharSequence, duration: Int) = Toast(text, duration)

    @JvmStatic fun makeText(context: Context, resId: Int, duration: Int) = Toast(context.getString(resId), duration)
  }
}
