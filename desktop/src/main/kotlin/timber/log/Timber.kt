package timber.log

/** Minimal JVM implementation of the Timber API surface used by upstream (Timber 5 is Android only). */
class Timber private constructor() {
  abstract class Tree {
    protected open fun isLoggable(tag: String?, priority: Int): Boolean = true

    protected open fun log(priority: Int, tag: String?, message: String, t: Throwable?) {}

    private fun dispatch(priority: Int, t: Throwable?, message: String?, args: Array<out Any?>) {
      val msg = formatMessage(message, args) ?: t?.stackTraceToString() ?: return
      if (isLoggable(null, priority)) log(priority, null, msg, t)
    }

    private fun formatMessage(message: String?, args: Array<out Any?>) =
        if (message != null && args.isNotEmpty()) message.format(*args) else message

    open fun v(message: String?, vararg args: Any?) = dispatch(2, null, message, args)

    open fun v(t: Throwable?, message: String?, vararg args: Any?) = dispatch(2, t, message, args)

    open fun d(message: String?, vararg args: Any?) = dispatch(3, null, message, args)

    open fun d(t: Throwable?, message: String?, vararg args: Any?) = dispatch(3, t, message, args)

    open fun i(message: String?, vararg args: Any?) = dispatch(4, null, message, args)

    open fun i(t: Throwable?, message: String?, vararg args: Any?) = dispatch(4, t, message, args)

    open fun w(message: String?, vararg args: Any?) = dispatch(5, null, message, args)

    open fun w(t: Throwable?, message: String?, vararg args: Any?) = dispatch(5, t, message, args)

    open fun e(message: String?, vararg args: Any?) = dispatch(6, null, message, args)

    open fun e(t: Throwable?, message: String?, vararg args: Any?) = dispatch(6, t, message, args)

    open fun wtf(message: String?, vararg args: Any?) = dispatch(7, null, message, args)

    open fun wtf(t: Throwable?, message: String?, vararg args: Any?) = dispatch(7, t, message, args)

    open fun v(t: Throwable?) = dispatch(2, t, null, emptyArray())
    open fun d(t: Throwable?) = dispatch(3, t, null, emptyArray())
    open fun i(t: Throwable?) = dispatch(4, t, null, emptyArray())
    open fun w(t: Throwable?) = dispatch(5, t, null, emptyArray())
    open fun e(t: Throwable?) = dispatch(6, t, null, emptyArray())
    open fun wtf(t: Throwable?) = dispatch(7, t, null, emptyArray())

    open fun log(priority: Int, message: String?, vararg args: Any?) = dispatch(priority, null, message, args)
  }

  open class DebugTree : Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
      val level = when (priority) { 2 -> "V"; 3 -> "D"; 4 -> "I"; 5 -> "W"; 6 -> "E"; else -> "A" }
      System.err.println("$level/${tag ?: "Neko"}: $message")
      t?.printStackTrace()
    }
  }

  companion object Forest : Tree() {
    private val trees = java.util.concurrent.CopyOnWriteArrayList<Tree>()

    @JvmStatic fun plant(tree: Tree) { trees.add(tree) }

    @JvmStatic fun uproot(tree: Tree) { trees.remove(tree) }

    @JvmStatic fun uprootAll() = trees.clear()

    @JvmStatic val treeCount: Int get() = trees.size
    @JvmStatic fun forest(): List<Tree> = trees.toList()
    @JvmStatic fun asTree(): Tree = this

    @JvmStatic fun tag(tag: String): Tree = this

    override fun v(message: String?, vararg args: Any?) = trees.forEach { it.v(message, *args) }
    override fun v(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.v(t, message, *args) }
    override fun d(message: String?, vararg args: Any?) = trees.forEach { it.d(message, *args) }
    override fun d(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.d(t, message, *args) }
    override fun i(message: String?, vararg args: Any?) = trees.forEach { it.i(message, *args) }
    override fun i(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.i(t, message, *args) }
    override fun w(message: String?, vararg args: Any?) = trees.forEach { it.w(message, *args) }
    override fun w(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.w(t, message, *args) }
    override fun e(message: String?, vararg args: Any?) = trees.forEach { it.e(message, *args) }
    override fun e(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.e(t, message, *args) }
    override fun wtf(message: String?, vararg args: Any?) = trees.forEach { it.wtf(message, *args) }
    override fun wtf(t: Throwable?, message: String?, vararg args: Any?) = trees.forEach { it.wtf(t, message, *args) }
  }
}
