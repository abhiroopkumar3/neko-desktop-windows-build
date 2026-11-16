package android.content

open class ContextWrapper(private val base: Context?) : Context() {
  fun getBaseContext(): Context? = base
}
