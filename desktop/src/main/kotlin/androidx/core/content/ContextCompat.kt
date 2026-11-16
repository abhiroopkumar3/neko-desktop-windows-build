package androidx.core.content

import android.content.Context

object ContextCompat {
  @JvmStatic fun getExternalFilesDirs(context: android.content.Context, type: String?): Array<java.io.File?> = context.getExternalFilesDirs(type)
  const val RECEIVER_EXPORTED = 2
  const val RECEIVER_NOT_EXPORTED = 4
  @JvmStatic fun getColor(context: Context, id: Int): Int = context.resources.getColor(id)
  @JvmStatic fun getDrawable(context: Context, id: Int): android.graphics.drawable.Drawable? = context.getDrawable(id)
  @JvmStatic fun checkSelfPermission(context: Context, permission: String): Int = 0
  @JvmStatic fun getMainExecutor(context: Context): java.util.concurrent.Executor = java.util.concurrent.Executor { it.run() }
}
