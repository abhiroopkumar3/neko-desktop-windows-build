package org.nekomanga.desktop.platform

import java.io.File

/** Per-user folders for the desktop app (the equivalent of Android's app-private storage). */
object AppPaths {
  val home: File by lazy {
    val base =
        System.getenv("APPDATA")?.let(::File) // Windows: C:\Users\<you>\AppData\Roaming
            ?: File(System.getProperty("user.home"), ".local/share")
    File(base, "Neko").also { it.mkdirs() }
  }
  val files: File by lazy { File(home, "files").also { it.mkdirs() } }
  val cache: File by lazy {
    val base = System.getenv("LOCALAPPDATA")?.let(::File) ?: File(System.getProperty("user.home"), ".cache")
    File(base, "Neko/cache").also { it.mkdirs() }
  }
}
