package org.nekomanga.desktop.selftest

import java.io.File
import kotlinx.coroutines.runBlocking
import org.nekomanga.data.database.entity.CategoryEntity
import org.nekomanga.desktop.platform.DesktopDatabase

/** Headless check that the shared Room database works on the JVM. Run: gradlew dbSelfTest */
fun main() = runBlocking {
  val file = File.createTempFile("neko-selftest", ".db").also { it.delete() }
  val db = DesktopDatabase.open(file)
  val dao = db.categoryDao()
  dao.insertCategory(CategoryEntity(name = "Reading", order = 0, flags = 0, mangaOrder = ""))
  val all = dao.getAllCategories()
  check(all.size == 1 && all[0].name == "Reading") { "unexpected categories: $all" }
  check(db.mangaDao().getMangaList().isEmpty())
  db.close()
  file.delete()
  println("DB SELFTEST OK: inserted and read ${all.size} category on bundled SQLite")
}
