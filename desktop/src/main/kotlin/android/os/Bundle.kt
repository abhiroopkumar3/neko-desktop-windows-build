package android.os

/** Map-backed android.os.Bundle. */
open class Bundle() {
  private val map = LinkedHashMap<String, Any?>()
  constructor(other: Bundle) : this() { map.putAll(other.map) }

  fun putString(k: String?, v: String?) { map[k.orEmpty()] = v }
  fun putInt(k: String?, v: Int) { map[k.orEmpty()] = v }
  fun putLong(k: String?, v: Long) { map[k.orEmpty()] = v }
  fun putBoolean(k: String?, v: Boolean) { map[k.orEmpty()] = v }
  fun putFloat(k: String?, v: Float) { map[k.orEmpty()] = v }
  fun putDouble(k: String?, v: Double) { map[k.orEmpty()] = v }
  fun putStringArray(k: String?, v: Array<String>?) { map[k.orEmpty()] = v }
  fun putStringArrayList(k: String?, v: ArrayList<String>?) { map[k.orEmpty()] = v }
  fun putLongArray(k: String?, v: LongArray?) { map[k.orEmpty()] = v }
  fun putBundle(k: String?, v: Bundle?) { map[k.orEmpty()] = v }

  fun get(k: String?): Any? = map[k]
  fun getString(k: String?): String? = map[k] as? String
  fun getString(k: String?, def: String): String = map[k] as? String ?: def
  fun getInt(k: String?, def: Int = 0): Int = map[k] as? Int ?: def
  fun getLong(k: String?, def: Long = 0L): Long = map[k] as? Long ?: def
  fun getBoolean(k: String?, def: Boolean = false): Boolean = map[k] as? Boolean ?: def
  fun getFloat(k: String?, def: Float = 0f): Float = map[k] as? Float ?: def
  fun getDouble(k: String?, def: Double = 0.0): Double = map[k] as? Double ?: def
  fun getStringArray(k: String?): Array<String>? = @Suppress("UNCHECKED_CAST") (map[k] as? Array<String>)
  fun getStringArrayList(k: String?): ArrayList<String>? = @Suppress("UNCHECKED_CAST") (map[k] as? ArrayList<String>)
  fun getLongArray(k: String?): LongArray? = map[k] as? LongArray
  fun getBundle(k: String?): Bundle? = map[k] as? Bundle

  fun putAny(k: String, v: Any?) { map[k] = v }
  fun getAny(k: String?): Any? = map[k]
  fun putAll(other: Bundle) { map.putAll(other.map) }
  fun containsKey(k: String?): Boolean = map.containsKey(k)
  fun remove(k: String?) { map.remove(k) }
  fun clear() = map.clear()
  fun isEmpty(): Boolean = map.isEmpty()
  fun keySet(): Set<String> = map.keys
  val size: Int get() = map.size

  companion object { @JvmField val EMPTY = Bundle() }
}
