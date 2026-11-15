package android.graphics

object Color {
  const val BLACK = 0xFF000000.toInt()
  const val WHITE = 0xFFFFFFFF.toInt()
  const val RED = 0xFFFF0000.toInt()
  const val GREEN = 0xFF00FF00.toInt()
  const val BLUE = 0xFF0000FF.toInt()
  const val GRAY = 0xFF888888.toInt()
  const val TRANSPARENT = 0

  @JvmStatic fun argb(a: Int, r: Int, g: Int, b: Int): Int = (a shl 24) or (r shl 16) or (g shl 8) or b
  @JvmStatic fun rgb(r: Int, g: Int, b: Int): Int = argb(255, r, g, b)
  @JvmStatic fun alpha(c: Int): Int = c ushr 24
  @JvmStatic fun red(c: Int): Int = (c shr 16) and 0xFF
  @JvmStatic fun green(c: Int): Int = (c shr 8) and 0xFF
  @JvmStatic fun blue(c: Int): Int = c and 0xFF

  @JvmStatic fun parseColor(s: String): Int {
    val named = mapOf("black" to BLACK, "white" to WHITE, "red" to RED, "green" to GREEN, "blue" to BLUE, "gray" to GRAY, "grey" to GRAY, "transparent" to TRANSPARENT)
    if (!s.startsWith("#")) return named[s.lowercase()] ?: throw IllegalArgumentException("Unknown color: $s")
    val hex = s.drop(1)
    return when (hex.length) {
      6 -> (0xFF000000L or hex.toLong(16)).toInt()
      8 -> hex.toLong(16).toInt()
      else -> throw IllegalArgumentException("Unknown color: $s")
    }
  }
}
