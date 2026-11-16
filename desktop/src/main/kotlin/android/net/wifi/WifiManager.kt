package android.net.wifi

class WifiManager {
  class WifiLock { fun acquire() {}; fun release() {}; val isHeld get() = false; fun setReferenceCounted(v: Boolean) {} }
  val isWifiEnabled: Boolean get() = true
  fun createWifiLock(mode: Int, tag: String) = WifiLock()
  companion object { const val WIFI_MODE_FULL_HIGH_PERF = 3; const val WIFI_MODE_FULL_LOW_LATENCY = 4 }
}
