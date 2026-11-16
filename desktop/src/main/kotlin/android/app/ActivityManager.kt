package android.app

class ActivityManager {
  val isLowRamDevice: Boolean get() = false
  val memoryClass: Int get() = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
}
