package org.nekomanga.desktop.compat

/** coil3.Image to the android.graphics.Bitmap the upstream notification code expects. */
fun coil3.Image.toBitmap(): android.graphics.Bitmap? {
  val sk = (this as? coil3.BitmapImage)?.bitmap ?: return null
  return skiaToAndroid(sk)
}

fun skiaToAndroid(sk: org.jetbrains.skia.Bitmap): android.graphics.Bitmap {
  val png = org.jetbrains.skia.Image.makeFromBitmap(sk).encodeToData(org.jetbrains.skia.EncodedImageFormat.PNG)!!.bytes
  return android.graphics.Bitmap(javax.imageio.ImageIO.read(java.io.ByteArrayInputStream(png)))
}
