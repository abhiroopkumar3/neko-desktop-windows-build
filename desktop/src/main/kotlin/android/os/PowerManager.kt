package android.os

class PowerManager {
  class WakeLock { fun acquire() {}; fun acquire(timeout: Long) {}; fun release() {}; val isHeld get() = false; fun setReferenceCounted(v: Boolean) {} }
  fun newWakeLock(level: Int, tag: String) = WakeLock()
  fun isIgnoringBatteryOptimizations(pkg: String) = true
  val isPowerSaveMode get() = false
  companion object { const val PARTIAL_WAKE_LOCK = 1 }
}
