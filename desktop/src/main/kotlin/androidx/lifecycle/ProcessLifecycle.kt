package androidx.lifecycle

/** The desktop process is "started" for its whole life; only closing the last window ends it. */
object ProcessLifecycleOwner {
  private val owner = object : LifecycleOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry(this).also { it.currentState = Lifecycle.State.RESUMED }
  }
  @JvmStatic fun get(): LifecycleOwner = owner
}
