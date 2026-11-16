package androidx.compose.ui.text.font

/** Font(R.font.x, weight): resolves the id to the bundled upstream .ttf. */
fun Font(resId: Int, weight: FontWeight = FontWeight.Normal, style: FontStyle = FontStyle.Normal): Font {
  val name = org.nekomanga.desktop.res.RTable.font[resId - org.nekomanga.desktop.res.RTable.FONT_BASE]
  return androidx.compose.ui.text.platform.Font(resource = "/android-res/font/$name.ttf", weight = weight, style = style)
}
