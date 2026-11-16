package androidx.biometric

import android.content.Context

/** Windows Hello is a later port; until then the app sees "no biometric hardware" and skips the lock. */
class BiometricManager private constructor() {
  fun canAuthenticate(authenticators: Int): Int = BIOMETRIC_ERROR_NO_HARDWARE
  object Authenticators {
    const val BIOMETRIC_STRONG = 0xF
    const val BIOMETRIC_WEAK = 0xFF
    const val DEVICE_CREDENTIAL = 0x8000
  }
  companion object {
    const val BIOMETRIC_SUCCESS = 0
    const val BIOMETRIC_ERROR_NO_HARDWARE = 12
    const val BIOMETRIC_ERROR_NONE_ENROLLED = 11
    @JvmStatic fun from(context: Context) = BiometricManager()
  }
}
