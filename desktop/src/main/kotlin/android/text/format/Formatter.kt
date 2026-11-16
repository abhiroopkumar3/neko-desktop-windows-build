package android.text.format

import android.content.Context

object Formatter {
  @JvmStatic
  fun formatFileSize(context: Context?, bytes: Long): String {
    if (bytes < 1000) return "$bytes B"
    val units = listOf("kB", "MB", "GB", "TB")
    var v = bytes / 1000.0; var i = 0
    while (v >= 1000 && i < units.lastIndex) { v /= 1000; i++ }
    return String.format("%.2f %s", v, units[i])
  }
}
