package android.graphics

import android.content.res.Resources
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO

object BitmapFactory {
  class Options {
    var inJustDecodeBounds = false
    var inSampleSize = 1
    var inPreferredConfig: Bitmap.Config? = null
    var inMutable = false
    var outWidth = 0
    var outHeight = 0
    var outMimeType: String? = null
  }

  @JvmStatic
  fun decodeStream(stream: InputStream?, outPadding: Rect? = null, opts: Options? = null): Bitmap? {
    stream ?: return null
    return runCatching {
      ImageIO.createImageInputStream(stream).use { iis ->
        val readers = ImageIO.getImageReaders(iis)
        if (!readers.hasNext()) return null
        val reader = readers.next()
        reader.input = iis
        opts?.let { it.outWidth = reader.getWidth(0); it.outHeight = reader.getHeight(0); it.outMimeType = reader.formatName }
        if (opts?.inJustDecodeBounds == true) { reader.dispose(); return null }
        val img = reader.read(0).let { src ->
          val sample = (opts?.inSampleSize ?: 1).coerceAtLeast(1)
          if (sample == 1) src else java.awt.image.BufferedImage(src.width / sample, src.height / sample, java.awt.image.BufferedImage.TYPE_INT_ARGB).also { dst ->
            dst.createGraphics().apply { drawImage(src, 0, 0, dst.width, dst.height, null); dispose() }
          }
        }
        reader.dispose()
        Bitmap(img)
      }
    }.getOrNull()
  }

  @JvmStatic fun decodeStream(stream: InputStream?): Bitmap? = decodeStream(stream, null, null)

  @JvmStatic
  fun decodeByteArray(data: ByteArray, offset: Int, length: Int, opts: Options? = null): Bitmap? =
      decodeStream(ByteArrayInputStream(data, offset, length), null, opts)

  @JvmStatic fun decodeByteArray(data: ByteArray, offset: Int, length: Int): Bitmap? = decodeByteArray(data, offset, length, null)

  @JvmStatic fun decodeResource(res: Resources?, id: Int): Bitmap? = null
}
