package coil3.transform

import android.graphics.Bitmap
import coil3.size.Size

/** Bitmap transformation hook from Coil 2/3 on Android; the desktop loader applies these while decoding reader pages. */
abstract class Transformation {
  abstract val cacheKey: String
  abstract suspend fun transform(input: Bitmap, size: Size): Bitmap
}
