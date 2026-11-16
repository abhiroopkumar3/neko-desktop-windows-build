package android.database.sqlite

import android.database.Cursor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** Read-only access to bundled SQLite files (e.g. the tracker id mapping database). */
class SQLiteDatabase private constructor(private val conn: SQLiteConnection) {
  var isOpen = true; private set
  fun rawQuery(sql: String, args: Array<String>?): Cursor? {
    val stmt = conn.prepare(sql)
    args?.forEachIndexed { i, a -> stmt.bindText(i + 1, a) }
    val rows = ArrayList<Array<String?>>()
    val n = stmt.getColumnCount()
    while (stmt.step()) rows.add(Array(n) { if (stmt.isNull(it)) null else stmt.getText(it) })
    stmt.close()
    return Cursor(rows)
  }
  fun close() { if (isOpen) { conn.close(); isOpen = false } }
  companion object {
    const val OPEN_READONLY = 1
    @JvmStatic fun openDatabase(path: String, factory: Any?, flags: Int): SQLiteDatabase = SQLiteDatabase(BundledSQLiteDriver().open(path))
  }
}
