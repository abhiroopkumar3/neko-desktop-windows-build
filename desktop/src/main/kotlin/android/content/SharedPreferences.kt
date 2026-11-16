package android.content

import java.io.File
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/** android.content.SharedPreferences contract. */
interface SharedPreferences {
  val all: Map<String, *>
  fun getString(key: String?, defValue: String?): String?
  fun getStringSet(key: String?, defValues: Set<String>?): Set<String>?
  fun getInt(key: String?, defValue: Int): Int
  fun getLong(key: String?, defValue: Long): Long
  fun getFloat(key: String?, defValue: Float): Float
  fun getBoolean(key: String?, defValue: Boolean): Boolean
  fun contains(key: String?): Boolean
  fun edit(): Editor
  fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener)
  fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener)

  fun interface OnSharedPreferenceChangeListener {
    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?)
  }

  interface Editor {
    fun putString(key: String, value: String?): Editor
    fun putStringSet(key: String, values: Set<String>?): Editor
    fun putInt(key: String, value: Int): Editor
    fun putLong(key: String, value: Long): Editor
    fun putFloat(key: String, value: Float): Editor
    fun putBoolean(key: String, value: Boolean): Editor
    fun remove(key: String): Editor
    fun clear(): Editor
    fun commit(): Boolean
    fun apply()
  }
}

/**
 * JSON-file backed SharedPreferences. Values keep their Android types; writes are atomic
 * (temp file + move), listeners fire after each change, and reads are served from memory.
 */
class FileSharedPreferences(private val file: File) : SharedPreferences {
  private val lock = Any()
  private val data = LinkedHashMap<String, Any>()
  private val listeners = CopyOnWriteArraySet<SharedPreferences.OnSharedPreferenceChangeListener>()

  init {
    runCatching {
      if (file.exists()) {
        Json.parseToJsonElement(file.readText()).jsonObject.forEach { (k, v) ->
          val o = v.jsonObject
          val value = o["v"]!!
          data[k] = when (o["t"]!!.jsonPrimitive.content) {
            "s" -> value.jsonPrimitive.content
            "i" -> value.jsonPrimitive.intOrNull ?: 0
            "l" -> value.jsonPrimitive.longOrNull ?: 0L
            "f" -> value.jsonPrimitive.floatOrNull ?: 0f
            "b" -> value.jsonPrimitive.booleanOrNull ?: false
            else -> value.jsonArray.map { it.jsonPrimitive.content }.toSet()
          }
        }
      }
    }.onFailure { System.err.println("Could not read preferences ${file.name}: $it") }
  }

  @Suppress("UNCHECKED_CAST")
  override val all: Map<String, *> get() = synchronized(lock) { HashMap(data) }
  override fun getString(key: String?, defValue: String?) = synchronized(lock) { data[key] as? String ?: defValue }
  @Suppress("UNCHECKED_CAST")
  override fun getStringSet(key: String?, defValues: Set<String>?) = synchronized(lock) { (data[key] as? Set<String>)?.toSet() ?: defValues }
  override fun getInt(key: String?, defValue: Int) = synchronized(lock) { data[key] as? Int ?: defValue }
  override fun getLong(key: String?, defValue: Long) = synchronized(lock) { data[key] as? Long ?: defValue }
  override fun getFloat(key: String?, defValue: Float) = synchronized(lock) { data[key] as? Float ?: defValue }
  override fun getBoolean(key: String?, defValue: Boolean) = synchronized(lock) { data[key] as? Boolean ?: defValue }
  override fun contains(key: String?) = synchronized(lock) { data.containsKey(key) }
  override fun edit(): SharedPreferences.Editor = EditorImpl()
  override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) { listeners.add(listener) }
  override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) { listeners.remove(listener) }

  private fun persist() {
    val snapshot = synchronized(lock) { LinkedHashMap(data) }
    val json = buildJsonObject {
      snapshot.forEach { (k, v) ->
        put(k, buildJsonObject {
          when (v) {
            is String -> { put("t", JsonPrimitive("s")); put("v", JsonPrimitive(v)) }
            is Int -> { put("t", JsonPrimitive("i")); put("v", JsonPrimitive(v)) }
            is Long -> { put("t", JsonPrimitive("l")); put("v", JsonPrimitive(v)) }
            is Float -> { put("t", JsonPrimitive("f")); put("v", JsonPrimitive(v)) }
            is Boolean -> { put("t", JsonPrimitive("b")); put("v", JsonPrimitive(v)) }
            is Set<*> -> { put("t", JsonPrimitive("ss")); put("v", buildJsonArray { v.forEach { add(JsonPrimitive(it.toString())) } }) }
          }
        })
      }
    }
    file.parentFile?.mkdirs()
    val tmp = File(file.parentFile, file.name + ".tmp")
    tmp.writeText(Json.encodeToString(JsonObject.serializer(), json))
    java.nio.file.Files.move(tmp.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
  }

  private inner class EditorImpl : SharedPreferences.Editor {
    private val pending = LinkedHashMap<String, Any?>()
    private var clearAll = false
    private fun put(k: String, v: Any?) = apply { synchronized(pending) { pending[k] = v } }
    override fun putString(key: String, value: String?) = put(key, value)
    override fun putStringSet(key: String, values: Set<String>?) = put(key, values?.toSet())
    override fun putInt(key: String, value: Int) = put(key, value)
    override fun putLong(key: String, value: Long) = put(key, value)
    override fun putFloat(key: String, value: Float) = put(key, value)
    override fun putBoolean(key: String, value: Boolean) = put(key, value)
    override fun remove(key: String) = put(key, null)
    override fun clear() = apply { clearAll = true }

    private fun commitToMemory(): List<String?> {
      val changed = ArrayList<String?>()
      synchronized(lock) {
        if (clearAll) { data.keys.toList().forEach { changed.add(it) }; data.clear() }
        synchronized(pending) {
          pending.forEach { (k, v) ->
            val had = data.containsKey(k)
            if (v == null) { if (had) { data.remove(k); changed.add(k) } }
            else if (data[k] != v) { data[k] = v; changed.add(k) }
          }
        }
      }
      return changed
    }

    private fun notify(keys: List<String?>) = keys.distinct().forEach { k -> listeners.forEach { it.onSharedPreferenceChanged(this@FileSharedPreferences, k) } }

    override fun commit(): Boolean {
      val changed = commitToMemory()
      val ok = runCatching { persist() }.onFailure { System.err.println("Could not save ${file.name}: $it") }.isSuccess
      notify(changed)
      return ok
    }

    override fun apply() {
      val changed = commitToMemory()
      writer.execute { runCatching { persist() }.onFailure { System.err.println("Could not save ${file.name}: $it") } }
      notify(changed)
    }
  }

  companion object {
    private val writer = Executors.newSingleThreadExecutor { r -> Thread(r, "prefs-writer").apply { isDaemon = true } }
  }
}
