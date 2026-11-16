package androidx.core.content

import android.content.Context

/** Returns the desktop stand-in for the requested service, or null when there is none. */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> Context.getSystemService(): T? = when (T::class) {
  android.app.ActivityManager::class -> android.app.ActivityManager()
  android.app.NotificationManager::class -> getSystemService(Context.NOTIFICATION_SERVICE)
  android.net.ConnectivityManager::class -> getSystemService(Context.CONNECTIVITY_SERVICE)
  android.os.PowerManager::class -> getSystemService(Context.POWER_SERVICE)
  android.content.ClipboardManager::class -> android.content.ClipboardManager()
  android.net.wifi.WifiManager::class -> getSystemService(Context.WIFI_SERVICE)
  else -> null
} as T?
