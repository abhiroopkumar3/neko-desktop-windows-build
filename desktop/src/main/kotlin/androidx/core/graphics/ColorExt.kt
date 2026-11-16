package androidx.core.graphics

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

val Int.alpha get() = this ushr 24
val Int.red get() = (this shr 16) and 0xFF
val Int.green get() = (this shr 8) and 0xFF
val Int.blue get() = this and 0xFF

object ColorUtils {
  @JvmStatic fun setAlphaComponent(color: Int, alpha: Int): Int = (color and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24)
  @JvmStatic fun blendARGB(c1: Int, c2: Int, ratio: Float): Int {
    val r = 1 - ratio
    fun mix(a: Int, b: Int) = (a * r + b * ratio).roundToInt().coerceIn(0, 255)
    return (mix(c1.alpha, c2.alpha) shl 24) or (mix(c1.red, c2.red) shl 16) or (mix(c1.green, c2.green) shl 8) or mix(c1.blue, c2.blue)
  }
  @JvmStatic fun calculateLuminance(color: Int): Double {
    fun ch(v: Int): Double { val s = v / 255.0; return if (s < 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4) }
    return 0.2126 * ch(color.red) + 0.7152 * ch(color.green) + 0.0722 * ch(color.blue)
  }
  @JvmStatic fun RGBToHSL(r: Int, g: Int, b: Int, hsl: FloatArray) {
    val rf = r / 255f; val gf = g / 255f; val bf = b / 255f
    val mx = max(rf, max(gf, bf)); val mn = min(rf, min(gf, bf)); val d = mx - mn
    var h: Float; val s: Float; val l = (mx + mn) / 2f
    if (mx == mn) { h = 0f; s = 0f } else {
      h = when (mx) { rf -> ((gf - bf) / d) % 6f; gf -> (bf - rf) / d + 2f; else -> (rf - gf) / d + 4f }
      s = d / (1f - abs(2f * l - 1f))
    }
    h = (h * 60f) % 360f; if (h < 0) h += 360f
    hsl[0] = h; hsl[1] = s.coerceIn(0f, 1f); hsl[2] = l.coerceIn(0f, 1f)
  }
  @JvmStatic fun colorToHSL(color: Int, hsl: FloatArray) = RGBToHSL(color.red, color.green, color.blue, hsl)
}

fun createBitmap(width: Int, height: Int, config: android.graphics.Bitmap.Config = android.graphics.Bitmap.Config.ARGB_8888): android.graphics.Bitmap =
    android.graphics.Bitmap.createBitmap(width, height, config)
