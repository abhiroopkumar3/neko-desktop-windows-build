package android.os

import java.io.File

object Environment {
  const val MEDIA_MOUNTED = "mounted"
  const val MEDIA_MOUNTED_READ_ONLY = "mounted_ro"
  const val DIRECTORY_PICTURES = "Pictures"
  const val DIRECTORY_DOWNLOADS = "Downloads"
  @JvmStatic fun getExternalStorageState(): String = MEDIA_MOUNTED
  @JvmStatic fun getExternalStorageState(f: java.io.File): String = MEDIA_MOUNTED
  @JvmStatic fun getExternalStorageDirectory(): File = File(System.getProperty("user.home"))
  @JvmStatic fun getExternalStoragePublicDirectory(type: String): File = File(System.getProperty("user.home"), type).also { it.mkdirs() }
}
