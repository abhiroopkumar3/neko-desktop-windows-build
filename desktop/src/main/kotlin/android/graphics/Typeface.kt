package android.graphics

class Typeface private constructor(val style: Int) {
  companion object {
    const val NORMAL = 0; const val BOLD = 1; const val ITALIC = 2; const val BOLD_ITALIC = 3
    @JvmField val DEFAULT = Typeface(NORMAL)
    @JvmField val DEFAULT_BOLD = Typeface(BOLD)
    @JvmStatic fun create(family: Typeface?, style: Int) = Typeface(style)
  }
}
