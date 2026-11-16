package androidx.core.content

import android.content.Context

object ContextCompat {
  const val RECEIVER_EXPORTED = 2
  const val RECEIVER_NOT_EXPORTED = 4
  @JvmStatic fun getColor(context: Context, id: Int): Int = context.resources.getColor(id)
  @JvmStatic fun checkSelfPermission(context: Context, permission: String): Int = 0
  @JvmStatic fun getMainExecutor(context: Context): java.util.concurrent.Executor = java.util.concurrent.Executor { it.run() }
}
