package android.content

import android.content.res.Resources
import java.io.File
import org.nekomanga.desktop.platform.AppPaths

/**
 * Desktop stand-in for android.content.Context. It exposes the parts of the Android API that the
 * shared upstream code relies on, backed by the Windows file system and bundled resources.
 */
open class Context {
  open val resources: Resources get() = Resources.instance
  open val applicationContext: Context get() = this
  open val packageName: String get() = org.nekomanga.BuildConfig.APPLICATION_ID
  open val filesDir: File get() = AppPaths.files
  open val cacheDir: File get() = AppPaths.cache
  open val externalCacheDir: File? get() = AppPaths.cache

  fun getString(id: Int): String = resources.getString(id)

  fun getString(id: Int, vararg formatArgs: Any?): String = resources.getString(id, *formatArgs)

  fun getText(id: Int): CharSequence = resources.getString(id)

  fun getDir(name: String, mode: Int = 0): File = File(AppPaths.home, name).also { it.mkdirs() }

  open fun getSystemService(name: String): Any? = null

  companion object {
    const val MODE_PRIVATE = 0
  }
}

/** Process-wide context used wherever upstream code asks for an application context. */
object DesktopContext : Context()
