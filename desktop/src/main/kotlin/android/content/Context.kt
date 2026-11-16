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

  open fun getSystemService(name: String): Any? = when (name) {
    NOTIFICATION_SERVICE -> android.app.NotificationManager()
    CONNECTIVITY_SERVICE -> connectivity
    POWER_SERVICE -> android.os.PowerManager()
    WIFI_SERVICE -> android.net.wifi.WifiManager()
    else -> null
  }

  val contentResolver: ContentResolver get() = ContentResolver
  val packageManager: android.content.pm.PackageManager get() = android.content.pm.PackageManager()
  open val assets: android.content.res.AssetManager get() = android.content.res.AssetManager
  fun getDrawable(id: Int): android.graphics.drawable.Drawable? = null
  fun obtainStyledAttributes(attrs: IntArray): android.content.res.TypedArray = android.content.res.TypedArray(attrs)
  val theme: Any? get() = null
  fun getDatabasePath(name: String): File = File(File(AppPaths.home, "databases").also { it.mkdirs() }, name)
  val applicationInfo: android.content.pm.ApplicationInfo get() = android.content.pm.ApplicationInfo()
  fun getExternalFilesDir(type: String?): File? = File(AppPaths.files, type ?: "external").also { it.mkdirs() }

  /** Hands links and files to the Windows shell; in-app navigation goes through the navigator instead. */
  open fun startActivity(intent: Intent) {
    val uri = intent.data ?: return
    runCatching {
      when (uri.scheme) {
        "http", "https", "mailto" -> java.awt.Desktop.getDesktop().browse(java.net.URI(uri.toString()))
        "file" -> uri.path?.let { java.awt.Desktop.getDesktop().open(File(it)) }
      }
    }.onFailure { System.err.println("Could not open $uri: $it") }
  }
  fun startService(intent: Intent): Any? = null
  fun sendBroadcast(intent: Intent) {}
  fun registerReceiver(receiver: Any?, filter: Any?): Any? = null
  fun unregisterReceiver(receiver: Any?) {}

  /** Per-name preference files under the app's data folder (see [FileSharedPreferences]). */
  fun getSharedPreferences(name: String, mode: Int = MODE_PRIVATE): SharedPreferences =
      sharedPreferenceCache.getOrPut(name) { FileSharedPreferences(File(File(AppPaths.home, "prefs"), "$name.json")) }

  companion object {
    const val MODE_PRIVATE = 0
    const val NOTIFICATION_SERVICE = "notification"
    const val CONNECTIVITY_SERVICE = "connectivity"
    const val POWER_SERVICE = "power"
    const val WIFI_SERVICE = "wifi"
    private val connectivity by lazy { android.net.ConnectivityManager() }
    private val sharedPreferenceCache = java.util.concurrent.ConcurrentHashMap<String, SharedPreferences>()
  }
}

/** Process-wide context used wherever upstream code asks for an application context. */
object DesktopContext : android.app.Application()
