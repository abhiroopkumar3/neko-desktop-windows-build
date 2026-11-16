package coil3

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asSkiaBitmap

/** Android view-drawable bridge: on desktop the pixels go through a java.awt image. */
fun Image.asDrawable(resources: Resources): Drawable {
  val sk = (this as? BitmapImage)?.bitmap
  val bmp = if (sk != null) org.nekomanga.desktop.compat.skiaToAndroid(sk) else android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
  return BitmapDrawable(resources, bmp)
}
