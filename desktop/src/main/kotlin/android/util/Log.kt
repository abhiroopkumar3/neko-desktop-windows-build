package android.util

object Log {
  const val VERBOSE = 2; const val DEBUG = 3; const val INFO = 4; const val WARN = 5; const val ERROR = 6; const val ASSERT = 7
  @JvmStatic fun println(priority: Int, tag: String?, msg: String): Int { System.err.println("[$tag] $msg"); return 0 }
  @JvmStatic fun d(tag: String?, msg: String?): Int = println(DEBUG, tag, msg.orEmpty())
  @JvmStatic fun i(tag: String?, msg: String?): Int = println(INFO, tag, msg.orEmpty())
  @JvmStatic fun w(tag: String?, msg: String?): Int = println(WARN, tag, msg.orEmpty())
  @JvmStatic fun e(tag: String?, msg: String?): Int = println(ERROR, tag, msg.orEmpty())
  @JvmStatic fun e(tag: String?, msg: String?, t: Throwable?): Int = println(ERROR, tag, msg.orEmpty() + (t?.let { "\n" + it.stackTraceToString() } ?: ""))
  @JvmStatic fun getStackTraceString(t: Throwable?): String = t?.stackTraceToString().orEmpty()
}
