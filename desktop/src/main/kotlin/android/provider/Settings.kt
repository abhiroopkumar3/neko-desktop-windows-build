package android.provider

object Settings {
  const val ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS"
  const val ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS"
  const val ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS"
  const val ACTION_MANAGE_UNKNOWN_APP_SOURCES = "android.settings.MANAGE_UNKNOWN_APP_SOURCES"
  const val EXTRA_APP_PACKAGE = "android.provider.extra.APP_PACKAGE"
  const val EXTRA_CHANNEL_ID = "android.provider.extra.CHANNEL_ID"
  object Secure { @JvmStatic fun getString(r: Any?, name: String): String? = null }
  object Global { @JvmStatic fun getString(r: Any?, name: String): String? = null; @JvmStatic fun getInt(r: Any?, name: String, d: Int) = d }
}
