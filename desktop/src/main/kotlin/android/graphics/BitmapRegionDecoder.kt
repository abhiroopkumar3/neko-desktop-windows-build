package android.graphics

import java.io.InputStream
import javax.imageio.ImageIO

/** Decodes crops of a page without materialising a full-size copy where the reader supports it. */
class BitmapRegionDecoder private constructor(private val bytes: ByteArray, val width: Int, val height: Int) {
  fun decodeRegion(rect: Rect, options: BitmapFactory.Options?): Bitmap {
    val sample = (options?.inSampleSize ?: 1).coerceAtLeast(1)
    val iis = ImageIO.createImageInputStream(bytes.inputStream())
    val reader = ImageIO.getImageReaders(iis).next()
    reader.input = iis
    val param = reader.defaultReadParam.apply {
      sourceRegion = java.awt.Rectangle(rect.left, rect.top, rect.width(), rect.height())
      setSourceSubsampling(sample, sample, 0, 0)
    }
    return try { Bitmap(reader.read(0, param)) } finally { reader.dispose(); iis.close() }
  }
  fun recycle() {}
  companion object {
    private fun size(b: ByteArray): Pair<Int, Int>? = runCatching {
      ImageIO.createImageInputStream(b.inputStream()).use { iis ->
        val r = ImageIO.getImageReaders(iis).next(); r.input = iis; (r.getWidth(0) to r.getHeight(0)).also { r.dispose() }
      }
    }.getOrNull()
    @JvmStatic fun newInstance(data: ByteArray, offset: Int, length: Int, isShareable: Boolean = false): BitmapRegionDecoder? {
      val b = data.copyOfRange(offset, offset + length); return size(b)?.let { BitmapRegionDecoder(b, it.first, it.second) }
    }
    @JvmStatic fun newInstance(stream: InputStream, isShareable: Boolean = false): BitmapRegionDecoder? {
      val b = stream.readBytes(); return size(b)?.let { BitmapRegionDecoder(b, it.first, it.second) }
    }
  }
}
