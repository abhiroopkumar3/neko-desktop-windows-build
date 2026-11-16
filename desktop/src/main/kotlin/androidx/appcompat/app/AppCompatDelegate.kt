package androidx.appcompat.app

object AppCompatDelegate {
  const val MODE_NIGHT_FOLLOW_SYSTEM = -1
  const val MODE_NIGHT_NO = 1
  const val MODE_NIGHT_YES = 2
  /** Desktop theme code reads this; the window applies it. */
  @Volatile private var mode: Int = MODE_NIGHT_FOLLOW_SYSTEM
  @JvmStatic fun setDefaultNightMode(mode: Int) { this.mode = mode }
  @JvmStatic fun getDefaultNightMode(): Int = mode
}
