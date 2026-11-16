package android.os

/** Desktop values for android.os.Build. SDK_INT reports a modern Android level so upstream feature checks take their current-API branch. */
object Build {
  const val DISPLAY = "desktop"
  object VERSION {
    const val SDK_INT = 34
    const val RELEASE = "14"
    const val INCREMENTAL = "desktop"
    const val CODENAME = "REL"
    const val SECURITY_PATCH = ""
  }

  object VERSION_CODES {
    const val N = 24
    const val N_MR1 = 25
    const val O = 26
    const val O_MR1 = 27
    const val P = 28
    const val Q = 29
    const val R = 30
    const val S = 31
    const val S_V2 = 32
    const val TIRAMISU = 33
    const val UPSIDE_DOWN_CAKE = 34
    const val VANILLA_ICE_CREAM = 35
  }

  val SUPPORTED_ABIS: Array<String> = arrayOf(System.getProperty("os.arch") ?: "x86_64")
  const val MANUFACTURER = "Desktop"
  const val MODEL = "Windows"
  const val BRAND = "Desktop"
  const val DEVICE = "desktop"
  const val PRODUCT = "desktop"
  const val FINGERPRINT = "desktop"
}
