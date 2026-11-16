package android.content

import android.net.Uri
import android.os.Bundle

/** Data holder for Android's Intent. Desktop code routes intents through its own navigator. */
open class Intent() {
  var action: String? = null
  var data: Uri? = null
  var type: String? = null
  var flags: Int = 0
  var component: Any? = null
  var `package`: String? = null
  val extras: Bundle = Bundle()

  constructor(action: String?) : this() { this.action = action }
  constructor(action: String?, uri: Uri?) : this() { this.action = action; this.data = uri }
  constructor(context: Context?, cls: Class<*>?) : this() { component = cls }

  fun setAction(a: String?) = apply { action = a }
  fun setData(u: Uri?) = apply { data = u }
  fun setType(t: String?) = apply { type = t }
  fun setDataAndType(u: Uri?, t: String?) = apply { data = u; type = t }
  fun setFlags(f: Int) = apply { flags = f }
  fun addFlags(f: Int) = apply { flags = flags or f }
  fun setClass(context: Context?, cls: Class<*>?) = apply { component = cls }
  fun setPackage(p: String?) = apply { `package` = p }
  fun putExtra(name: String, value: Any?) = apply { extras.putAny(name, value) }
  fun putExtras(b: Bundle) = apply { extras.putAll(b) }
  fun hasExtra(name: String) = extras.containsKey(name)
  fun getStringExtra(name: String): String? = extras.getString(name)
  fun getIntExtra(name: String, def: Int): Int = extras.getInt(name, def)
  fun getLongExtra(name: String, def: Long): Long = extras.getLong(name, def)
  fun getBooleanExtra(name: String, def: Boolean): Boolean = extras.getBoolean(name, def)
  fun getStringArrayListExtra(name: String): ArrayList<String>? = extras.getAny(name) as? ArrayList<String>

  companion object {
    const val ACTION_VIEW = "android.intent.action.VIEW"
    const val ACTION_SEND = "android.intent.action.SEND"
    const val ACTION_SEARCH = "android.intent.action.SEARCH"
    const val ACTION_MAIN = "android.intent.action.MAIN"
    const val EXTRA_STREAM = "android.intent.extra.STREAM"
    const val EXTRA_TEXT = "android.intent.extra.TEXT"
    const val EXTRA_TITLE = "android.intent.extra.TITLE"
    const val FLAG_GRANT_READ_URI_PERMISSION = 0x1
    const val FLAG_GRANT_WRITE_URI_PERMISSION = 0x2
    const val FLAG_ACTIVITY_NEW_TASK = 0x10000000
    const val FLAG_ACTIVITY_CLEAR_TOP = 0x04000000
    const val FLAG_ACTIVITY_SINGLE_TOP = 0x20000000
    const val FLAG_ACTIVITY_CLEAR_TASK = 0x00008000
    @JvmStatic fun createChooser(target: Intent, title: CharSequence?): Intent = target
  }
}
