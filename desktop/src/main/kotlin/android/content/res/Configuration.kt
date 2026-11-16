package android.content.res

import java.awt.Toolkit

/** Window size in dp. Main updates it as the window resizes; the default is the primary screen. */
class Configuration(
    var screenWidthDp: Int = Toolkit.getDefaultToolkit().screenSize.width,
    var screenHeightDp: Int = Toolkit.getDefaultToolkit().screenSize.height,
    var orientation: Int = ORIENTATION_LANDSCAPE,
    var uiMode: Int = 0,
) {
  fun setTo(o: Configuration) { screenWidthDp = o.screenWidthDp; screenHeightDp = o.screenHeightDp; orientation = o.orientation; uiMode = o.uiMode; layoutDirection = o.layoutDirection }
  fun updateFrom(o: Configuration) = setTo(o)
  var layoutDirection: Int = LAYOUT_DIRECTION_LTR
  var smallestScreenWidthDp: Int = minOf(screenWidthDp, screenHeightDp)
  var fontScale: Float = 1f
  var locale: java.util.Locale = java.util.Locale.getDefault()
  companion object {
    const val LAYOUT_DIRECTION_LTR = 0
    const val LAYOUT_DIRECTION_RTL = 1
    const val ORIENTATION_PORTRAIT = 1
    const val ORIENTATION_LANDSCAPE = 2
    const val UI_MODE_NIGHT_MASK = 0x30
    const val UI_MODE_NIGHT_YES = 0x20
  }
}
