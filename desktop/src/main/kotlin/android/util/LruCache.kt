package android.util

open class LruCache<K : Any, V : Any>(private var maxSize: Int) {
  private val map = object : LinkedHashMap<K, V>(16, 0.75f, true) {}
  private var size = 0

  protected open fun sizeOf(key: K, value: V): Int = 1
  protected open fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {}

  @Synchronized fun get(key: K): V? = map[key]
  @Synchronized fun put(key: K, value: V): V? {
    size += sizeOf(key, value)
    val prev = map.put(key, value)
    if (prev != null) { size -= sizeOf(key, prev); entryRemoved(false, key, prev, value) }
    trimToSize(maxSize)
    return prev
  }
  @Synchronized fun remove(key: K): V? = map.remove(key)?.also { size -= sizeOf(key, it); entryRemoved(false, key, it, null) }
  @Synchronized fun evictAll() = trimToSize(-1)
  @Synchronized fun size(): Int = size
  @Synchronized fun maxSize(): Int = maxSize
  @Synchronized fun resize(max: Int) { maxSize = max; trimToSize(max) }
  @Synchronized fun snapshot(): Map<K, V> = LinkedHashMap(map)

  fun trimToSize(max: Int) {
    val it = map.entries.iterator()
    while (size > max && it.hasNext()) {
      val e = it.next(); it.remove(); size -= sizeOf(e.key, e.value); entryRemoved(true, e.key, e.value, null)
    }
  }
}
