package android.graphics

import android.content.res.Resources
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO

object BitmapDecoding {
  @JvmStatic
  fun decodeStream(stream: InputStream?, opts: BitmapFactory.Options?): Bitmap? {
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

}
