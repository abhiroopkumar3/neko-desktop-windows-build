package android.graphics

import java.awt.image.BufferedImage
import java.io.OutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/** android.graphics.Bitmap over a BufferedImage (ARGB). recycle() is a no-op; the JVM collects it. */
class Bitmap(val image: BufferedImage) {
  enum class Config { ARGB_8888, RGB_565, ALPHA_8, RGBA_F16, HARDWARE }
  enum class CompressFormat { JPEG, PNG, WEBP, WEBP_LOSSY, WEBP_LOSSLESS }

  val width: Int get() = image.width
  val height: Int get() = image.height
  var config: Config = Config.ARGB_8888
  val isRecycled: Boolean get() = false
  fun recycle() {}
  fun hasAlpha(): Boolean = image.colorModel.hasAlpha()
  fun getPixel(x: Int, y: Int): Int = image.getRGB(x, y)
  fun setPixel(x: Int, y: Int, color: Int) = image.setRGB(x, y, color)
  fun getPixels(pixels: IntArray, offset: Int, stride: Int, x: Int, y: Int, w: Int, h: Int) {
    image.getRGB(x, y, w, h, pixels, offset, stride)
  }
  fun copy(config: Config, mutable: Boolean): Bitmap = createBitmap(this)
  val colorSpace: ColorSpace? get() = ColorSpace.get(ColorSpace.Named.SRGB)
  val isMutable: Boolean get() = true
  val byteCount: Int get() = width * height * 4
  val allocationByteCount: Int get() = byteCount
  fun setHasAlpha(v: Boolean) {}
  fun eraseColor(color: Int) { val g = image.createGraphics(); g.composite = java.awt.AlphaComposite.Src; g.color = java.awt.Color(color, true); g.fillRect(0, 0, width, height); g.dispose() }

  fun compress(format: CompressFormat, quality: Int, out: OutputStream): Boolean {
    val (name, flat) = when (format) {
      CompressFormat.JPEG -> "jpg" to true
      CompressFormat.PNG -> "png" to false
      else -> "png" to false // no WebP writer in the JDK; lossless PNG is the safe stand-in
    }
    val src = if (flat) BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).also { it.createGraphics().apply { drawImage(image, 0, 0, java.awt.Color.WHITE, null); dispose() } } else image
    val writer = ImageIO.getImageWritersByFormatName(name).next()
    return runCatching {
      ImageIO.createImageOutputStream(out).use { ios ->
        writer.output = ios
        val params = writer.defaultWriteParam
        if (flat) { params.compressionMode = ImageWriteParam.MODE_EXPLICIT; params.compressionQuality = quality / 100f }
        writer.write(null, IIOImage(src, null, null), params)
      }
      true
    }.getOrDefault(false).also { writer.dispose() }
  }

  companion object {
    @JvmStatic fun createBitmap(w: Int, h: Int, config: Config): Bitmap = Bitmap(BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)).also { it.config = config }
    @JvmStatic fun createBitmap(w: Int, h: Int, config: Config, hasAlpha: Boolean, cs: ColorSpace?): Bitmap = createBitmap(w, h, config)
    @JvmStatic fun createBitmap(src: Bitmap, x: Int, y: Int, w: Int, h: Int, m: Matrix?, filter: Boolean): Bitmap {
      val t = m?.toTransform() ?: java.awt.geom.AffineTransform()
      val sub = src.image.getSubimage(x, y, w, h)
      val bounds = t.createTransformedShape(java.awt.Rectangle(0, 0, w, h)).bounds
      val out = BufferedImage(bounds.width.coerceAtLeast(1), bounds.height.coerceAtLeast(1), BufferedImage.TYPE_INT_ARGB)
      val g = out.createGraphics()
      g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, if (filter) java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR else java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
      g.translate(-bounds.x, -bounds.y); g.drawImage(sub, t, null); g.dispose()
      return Bitmap(out)
    }
    @JvmStatic fun createBitmap(src: Bitmap): Bitmap = Bitmap(BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB).also { it.createGraphics().apply { drawImage(src.image, 0, 0, null); dispose() } })
    @JvmStatic fun createBitmap(src: Bitmap, x: Int, y: Int, w: Int, h: Int): Bitmap =
        Bitmap(BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB).also { it.createGraphics().apply { drawImage(src.image.getSubimage(x, y, w, h), 0, 0, null); dispose() } })
    @JvmStatic fun createScaledBitmap(src: Bitmap, w: Int, h: Int, filter: Boolean): Bitmap =
        Bitmap(BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB).also {
          it.createGraphics().apply {
            setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, if (filter) java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC else java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
            drawImage(src.image, 0, 0, w, h, null); dispose()
          }
        })
  }
}
