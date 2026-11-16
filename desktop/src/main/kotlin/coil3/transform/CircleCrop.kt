package coil3.transform

import android.graphics.Bitmap
import coil3.size.Size

class CircleCropTransformation : Transformation() {
  override val cacheKey: String = CircleCropTransformation::class.java.name
  override suspend fun transform(input: Bitmap, size: Size): Bitmap {
    val d = minOf(input.width, input.height)
    val out = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
    val g = out.image.createGraphics()
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
    g.clip = java.awt.geom.Ellipse2D.Float(0f, 0f, d.toFloat(), d.toFloat())
    g.drawImage(input.image, (d - input.width) / 2, (d - input.height) / 2, null)
    g.dispose()
    return out
  }
}
