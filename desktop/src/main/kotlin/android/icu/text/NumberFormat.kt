package android.icu.text

/** ICU's NumberFormat API over java.text.NumberFormat. */
class NumberFormat private constructor(private val inner: java.text.NumberFormat) {
  fun format(n: Long): String = inner.format(n)
  fun format(n: Double): String = inner.format(n)
  fun setMaximumFractionDigits(n: Int) { inner.maximumFractionDigits = n }
  fun setMinimumFractionDigits(n: Int) { inner.minimumFractionDigits = n }
  companion object {
    const val NUMBERSTYLE = 0
    const val PERCENTSTYLE = 2
    @JvmStatic fun getInstance(style: Int): NumberFormat = if (style == PERCENTSTYLE) getPercentInstance() else getInstance()
    @JvmStatic fun getInstance(): NumberFormat = NumberFormat(java.text.NumberFormat.getInstance())
    @JvmStatic fun getInstance(locale: java.util.Locale): NumberFormat = NumberFormat(java.text.NumberFormat.getInstance(locale))
    @JvmStatic fun getCompactInstance(): NumberFormat = NumberFormat(java.text.NumberFormat.getCompactNumberInstance())
    @JvmStatic fun getPercentInstance(): NumberFormat = NumberFormat(java.text.NumberFormat.getPercentInstance())
  }
}
