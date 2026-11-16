package tachiyomi.decoder

import android.graphics.Bitmap
import android.graphics.Rect
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Replaces the native image decoder used on Android. JPEG, PNG, GIF and BMP decode through the JDK;
 * WebP, AVIF, HEIF and JXL are recognised by their signature, and decoding is plugged in later.
 */
enum class Format { Avif, Gif, Heif, Jpeg, Jxl, Png, Webp, Bmp }

class ImageType(val format: Format, val isAnimated: Boolean)

class ImageDecoder private constructor(private val bytes: ByteArray, val width: Int, val height: Int) {
  fun decode(sampleSize: Int = 1, rect: Rect? = null): Bitmap? = runCatching {
    val iis = ImageIO.createImageInputStream(bytes.inputStream())
    val reader = ImageIO.getImageReaders(iis).next()
    reader.input = iis
    val param = reader.defaultReadParam.apply {
      if (rect != null) sourceRegion = java.awt.Rectangle(rect.left, rect.top, rect.width(), rect.height())
      val s = sampleSize.coerceAtLeast(1); setSourceSubsampling(s, s, 0, 0)
    }
    try { Bitmap(reader.read(0, param)) } finally { reader.dispose(); iis.close() }
  }.getOrNull()

  fun decodeRegion(rect: Rect, options: android.graphics.BitmapFactory.Options?): Bitmap? = decode(options?.inSampleSize ?: 1, rect)
  fun recycle() {}

  companion object {
    @JvmStatic fun newInstance(stream: InputStream): ImageDecoder? = runCatching {
      val b = stream.readBytes()
      ImageIO.createImageInputStream(b.inputStream()).use { iis ->
        val r = ImageIO.getImageReaders(iis).next(); r.input = iis
        ImageDecoder(b, r.getWidth(0), r.getHeight(0)).also { r.dispose() }
      }
    }.getOrNull()

    @JvmStatic fun findType(b: ByteArray): ImageType? {
      fun at(i: Int, vararg v: Int) = b.size > i + v.size && v.indices.all { (b[i + it].toInt() and 0xFF) == v[it] }
      fun str(i: Int, s: String) = b.size >= i + s.length && String(b, i, s.length, Charsets.ISO_8859_1) == s
      return when {
        at(0, 0xFF, 0xD8, 0xFF) -> ImageType(Format.Jpeg, false)
        at(0, 0x89, 0x50, 0x4E, 0x47) -> ImageType(Format.Png, false)
        str(0, "GIF8") -> ImageType(Format.Gif, true)
        str(0, "RIFF") && str(8, "WEBP") -> ImageType(Format.Webp, str(12, "VP8X") && b.size > 20 && (b[20].toInt() and 0x02) != 0)
        str(4, "ftypavif") || str(4, "ftypavis") -> ImageType(Format.Avif, false)
        str(4, "ftypheic") || str(4, "ftypheix") || str(4, "ftypmif1") -> ImageType(Format.Heif, false)
        at(0, 0xFF, 0x0A) || at(0, 0x00, 0x00, 0x00, 0x0C, 0x4A, 0x58, 0x4C) -> ImageType(Format.Jxl, false)
        str(0, "BM") -> ImageType(Format.Bmp, false)
        else -> null
      }
    }
  }
}
