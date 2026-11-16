package org.nekomanga.desktop.selftest

import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import android.content.Context
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.nekomanga.data.database.entity.CategoryEntity
import org.nekomanga.desktop.platform.DesktopDatabase

/** Headless check that the shared Room database works on the JVM. Run: gradlew dbSelfTest */
class SelfTestWorker(c: Context, p: WorkerParameters) : CoroutineWorker(c, p) {
  override suspend fun doWork(): Result { ran = true; return Result.success() }
  companion object { @Volatile var ran = false }
}

fun main() = runBlocking {
  val file = File.createTempFile("neko-selftest", ".db").also { it.delete() }
  val db = DesktopDatabase.open(file)
  val dao = db.categoryDao()
  dao.insertCategory(CategoryEntity(name = "Reading", order = 0, flags = 0, mangaOrder = ""))
  val all = dao.getAllCategories()
  check(all.size == 1 && all[0].name == "Reading") { "unexpected categories: $all" }
  check(db.mangaDao().getMangaList().isEmpty())
  // Transaction rolls back when the block throws, commits otherwise.
  runCatching { db.withTransaction { dao.insertCategory(CategoryEntity(name = "Temp", order = 1, flags = 0, mangaOrder = "")); error("boom") } }
  check(dao.getAllCategories().size == 1) { "transaction did not roll back" }
  db.withTransaction { dao.insertCategory(CategoryEntity(name = "Done", order = 2, flags = 0, mangaOrder = "")) }
  check(dao.getAllCategories().size == 2) { "transaction did not commit" }
  db.close()
  file.delete()
  val wm = WorkManager.getInstance(android.content.DesktopContext)
  wm.enqueueUniqueWork("selftest", ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<SelfTestWorker>().addTag("t").build())
  repeat(50) { if (!SelfTestWorker.ran) delay(100) }
  delay(200)
  check(SelfTestWorker.ran) { "worker did not run" }
  check(wm.getWorkInfosByTag("t").get().single().state == WorkInfo.State.SUCCEEDED) { "worker state wrong" }
  println("DB SELFTEST OK: Room insert/read, transaction rollback and commit, WorkManager one-time work")
}
