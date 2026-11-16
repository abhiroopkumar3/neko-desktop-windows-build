package android.database

class Cursor(private val rows: List<Array<String?>>) : AutoCloseable {
  private var pos = -1
  fun moveToFirst(): Boolean { pos = 0; return rows.isNotEmpty() }
  fun moveToNext(): Boolean { pos++; return pos < rows.size }
  fun getString(i: Int): String? = rows.getOrNull(pos)?.getOrNull(i)
  fun getInt(i: Int): Int = getString(i)?.toIntOrNull() ?: 0
  fun getLong(i: Int): Long = getString(i)?.toLongOrNull() ?: 0
  val count: Int get() = rows.size
  override fun close() {}
}
