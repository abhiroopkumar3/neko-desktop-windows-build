package android.content.res

import java.awt.Toolkit

/** Window size in dp. Main updates it as the window resizes; the default is the primary screen. */
class Configuration(
    var screenWidthDp: Int = Toolkit.getDefaultToolkit().screenSize.width,
    var screenHeightDp: Int = Toolkit.getDefaultToolkit().screenSize.height,
    var orientation: Int = ORIENTATION_LANDSCAPE,
    var uiMode: Int = 0,
) {
  companion object {
    const val ORIENTATION_PORTRAIT = 1
    const val ORIENTATION_LANDSCAPE = 2
    const val UI_MODE_NIGHT_MASK = 0x30
    const val UI_MODE_NIGHT_YES = 0x20
  }
}
