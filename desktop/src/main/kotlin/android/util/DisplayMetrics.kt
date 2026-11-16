package android.util

import java.awt.Toolkit

class DisplayMetrics {
  var density: Float = Toolkit.getDefaultToolkit().screenResolution / 160f
  var scaledDensity: Float = density
  var densityDpi: Int = Toolkit.getDefaultToolkit().screenResolution
  var widthPixels: Int = Toolkit.getDefaultToolkit().screenSize.width
  var heightPixels: Int = Toolkit.getDefaultToolkit().screenSize.height
}
