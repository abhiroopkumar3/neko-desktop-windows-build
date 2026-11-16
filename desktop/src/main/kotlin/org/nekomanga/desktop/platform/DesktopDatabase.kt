package org.nekomanga.desktop.platform

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File
import kotlinx.coroutines.Dispatchers
import org.nekomanga.data.database.AppDatabase

/**
 * Builds the shared Room database on desktop: same schema (v46) as the Android app, stored in the
 * per-user data folder and driven by the bundled SQLite (no system SQLite needed).
 * Upstream's 45 -> 46 migration only upgrades legacy Android installs, so a fresh desktop
 * database starts at the current version.
 */
object DesktopDatabase {
  fun open(file: File = File(AppPaths.home, AppDatabase.DATABASE_NAME)): AppDatabase {
    file.parentFile?.mkdirs()
    return Room.databaseBuilder<AppDatabase>(name = file.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
  }
}
