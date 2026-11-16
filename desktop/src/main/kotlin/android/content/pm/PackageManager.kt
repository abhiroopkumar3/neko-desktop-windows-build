package android.content.pm

class ResolveInfo { val activityInfo: ActivityInfoName? = null }
class ActivityInfoName { val packageName: String? = null }

class PackageInfo { val versionName: String = org.nekomanga.BuildConfig.VERSION_NAME; val longVersionCode: Long = org.nekomanga.BuildConfig.VERSION_CODE.toLong() }

class PackageManager {
  class NameNotFoundException : Exception()
  fun hasSystemFeature(name: String): Boolean = true
  fun getPackageInfo(pkg: String, flags: Int): PackageInfo = PackageInfo()
  fun getLaunchIntentForPackage(pkg: String): android.content.Intent = android.content.Intent()
  fun canRequestPackageInstalls(): Boolean = true
  fun resolveActivity(intent: android.content.Intent, flags: Int): ResolveInfo? = null
  companion object {
    const val PERMISSION_GRANTED = 0
    const val PERMISSION_DENIED = -1
    const val GET_META_DATA = 128
    const val FEATURE_WEBVIEW = "android.software.webview"
    const val MATCH_DEFAULT_ONLY = 65536
  }
}

object ServiceInfo {
  const val FOREGROUND_SERVICE_TYPE_DATA_SYNC = 1
  const val FOREGROUND_SERVICE_TYPE_SPECIAL_USE = 1073741824
}
