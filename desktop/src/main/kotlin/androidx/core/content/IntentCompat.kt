package androidx.core.content

import android.content.Intent

object IntentCompat {
  @JvmStatic fun <T> getParcelableExtra(intent: Intent, name: String, clazz: Class<T>): T? = clazz.cast(intent.extras?.get(name))
}
