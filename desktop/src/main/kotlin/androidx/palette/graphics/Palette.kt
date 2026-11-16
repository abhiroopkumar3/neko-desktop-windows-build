package androidx.palette.graphics

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import kotlin.math.abs

/** Cover-colour extraction: quantises pixels into buckets and picks dominant, vibrant and muted swatches. */
class Palette private constructor(val swatches: List<Swatch>) {
  class Swatch(val rgb: Int, val population: Int) {
    val hsl: FloatArray by lazy { FloatArray(3).also { ColorUtils.colorToHSL(rgb, it) } }
  }
  val dominantSwatch: Swatch? get() = swatches.maxByOrNull { it.population }
  val vibrantSwatch: Swatch? get() = pick(0.5f, 0.8f, 0.3f, 0.7f)
  val mutedSwatch: Swatch? get() = pick(0.3f, 0.4f, 0.3f, 0.7f)
  val lightVibrantSwatch: Swatch? get() = pick(0.74f, 0.9f, 0.55f, 1f)
  val darkVibrantSwatch: Swatch? get() = pick(0.26f, 0.9f, 0f, 0.45f)
  val lightMutedSwatch: Swatch? get() = pick(0.74f, 0.3f, 0.55f, 1f)
  val darkMutedSwatch: Swatch? get() = pick(0.26f, 0.3f, 0f, 0.45f)

  private fun pick(targetL: Float, targetS: Float, minL: Float, maxL: Float): Swatch? =
      swatches.filter { it.hsl[2] in minL..maxL && it.hsl[1] >= 0.1f }
          .maxByOrNull { 3 * (1 - abs(it.hsl[1] - targetS)) + 6 * (1 - abs(it.hsl[2] - targetL)) + 1.0f * it.population / swatches.sumOf { s -> s.population } }

  class Builder(private val bitmap: Bitmap) {
    fun generate(): Palette {
      val scaled = if (bitmap.width * bitmap.height > 12544) Bitmap.createScaledBitmap(bitmap, 112, 112 * bitmap.height / bitmap.width.coerceAtLeast(1) + 1, true) else bitmap
      val counts = HashMap<Int, IntArray>() // bucket -> [pop, r, g, b]
      for (y in 0 until scaled.height) for (x in 0 until scaled.width) {
        val c = scaled.getPixel(x, y)
        if ((c ushr 24) < 128) continue
        val r = (c shr 16) and 0xFF; val g = (c shr 8) and 0xFF; val b = c and 0xFF
        val key = ((r shr 4) shl 8) or ((g shr 4) shl 4) or (b shr 4)
        val e = counts.getOrPut(key) { IntArray(4) }
        e[0]++; e[1] += r; e[2] += g; e[3] += b
      }
      val list = counts.values.sortedByDescending { it[0] }.take(16).map {
        Swatch((0xFF shl 24) or ((it[1] / it[0]) shl 16) or ((it[2] / it[0]) shl 8) or (it[3] / it[0]), it[0])
      }
      return Palette(list)
    }
    fun generate(callback: (Palette?) -> Unit) = callback(generate())
  }
  fun interface PaletteAsyncListener { fun onGenerated(p: Palette?) }
  companion object { @JvmStatic fun from(bitmap: Bitmap) = Builder(bitmap) }
}
