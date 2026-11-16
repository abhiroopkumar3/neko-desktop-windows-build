package android.content.pm

class PackageManager {
  companion object {
    const val PERMISSION_GRANTED = 0
    const val PERMISSION_DENIED = -1
    const val GET_META_DATA = 128
  }
}

object ServiceInfo {
  const val FOREGROUND_SERVICE_TYPE_DATA_SYNC = 1
  const val FOREGROUND_SERVICE_TYPE_SPECIAL_USE = 1073741824
}
