package android.graphics

import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.RenderingHints

/** Subset of android.graphics.Canvas drawing onto a [Bitmap] through Java 2D. */
class Canvas(val bitmap: Bitmap) {
  private fun g(paint: Paint?): Graphics2D = bitmap.image.createGraphics().also {
    if (paint?.isFilterBitmap != false) it.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    if (paint?.isAntiAlias == true) it.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  }
  val width get() = bitmap.width
  val height get() = bitmap.height

  fun drawColor(color: Int) { val g = g(null); g.composite = AlphaComposite.SrcOver; g.color = java.awt.Color(color, true); g.fillRect(0, 0, width, height); g.dispose() }
  fun drawRect(l: Float, t: Float, r: Float, b: Float, paint: Paint) { val g = g(paint); g.color = java.awt.Color(paint.color, true); g.fillRect(l.toInt(), t.toInt(), (r - l).toInt(), (b - t).toInt()); g.dispose() }
  fun drawRect(rect: Rect, paint: Paint) = drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
  fun drawRect(rect: RectF, paint: Paint) = drawRect(rect.left, rect.top, rect.right, rect.bottom, paint)
  fun drawBitmap(src: Bitmap, srcRect: Rect?, dst: Rect, paint: Paint?) {
    val g = g(paint); val s = srcRect ?: Rect(0, 0, src.width, src.height)
    g.drawImage(src.image, dst.left, dst.top, dst.right, dst.bottom, s.left, s.top, s.right, s.bottom, null); g.dispose()
  }
  fun drawBitmap(src: Bitmap, left: Float, top: Float, paint: Paint?) { val g = g(paint); g.drawImage(src.image, left.toInt(), top.toInt(), null); g.dispose() }
  fun drawBitmap(src: Bitmap, matrix: Matrix, paint: Paint?) { val g = g(paint); g.drawImage(src.image, matrix.toTransform(), null); g.dispose() }
}

class Paint(flags: Int = 0) {
  var color: Int = Color.BLACK
  var style: Style = Style.FILL
  var isAntiAlias: Boolean = flags and ANTI_ALIAS_FLAG != 0
  var isFilterBitmap: Boolean = flags and FILTER_BITMAP_FLAG != 0
  var strokeWidth: Float = 0f
  var alpha: Int = 255
  enum class Style { FILL, STROKE, FILL_AND_STROKE }
  companion object { const val ANTI_ALIAS_FLAG = 1; const val FILTER_BITMAP_FLAG = 2; const val DITHER_FLAG = 4 }
}

class RectF(var left: Float = 0f, var top: Float = 0f, var right: Float = 0f, var bottom: Float = 0f) {
  fun width() = right - left
  fun height() = bottom - top
}

class Matrix {
  private val t = java.awt.geom.AffineTransform()
  fun postRotate(degrees: Float) { t.preConcatenate(java.awt.geom.AffineTransform.getRotateInstance(Math.toRadians(degrees.toDouble()))) }
  fun postScale(sx: Float, sy: Float) { t.preConcatenate(java.awt.geom.AffineTransform.getScaleInstance(sx.toDouble(), sy.toDouble())) }
  fun postTranslate(dx: Float, dy: Float) { t.preConcatenate(java.awt.geom.AffineTransform.getTranslateInstance(dx.toDouble(), dy.toDouble())) }
  fun setScale(sx: Float, sy: Float) { t.setToScale(sx.toDouble(), sy.toDouble()) }
  fun reset() { t.setToIdentity() }
  fun toTransform() = java.awt.geom.AffineTransform(t)
}
