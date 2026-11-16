package androidx.appcompat.content.res

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object AppCompatResources {
  @JvmStatic fun getDrawable(context: Context, resId: Int): Drawable? = ContextCompat.getDrawable(context, resId)
}
