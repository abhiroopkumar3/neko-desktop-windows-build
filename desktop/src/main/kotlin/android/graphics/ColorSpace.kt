package android.graphics

/** Everything on desktop is sRGB; this keeps colour-space aware upstream code compiling. */
open class ColorSpace private constructor() {
  enum class Named { SRGB, DISPLAY_P3 }
  enum class Model { RGB, XYZ, LAB, CMYK }
  open val model: Model get() = Model.RGB
  class Rgb : ColorSpace() { val transferParameters: Any? = Any() }
  companion object { private val srgb = Rgb(); @JvmStatic fun get(named: Named): ColorSpace = srgb }
}
