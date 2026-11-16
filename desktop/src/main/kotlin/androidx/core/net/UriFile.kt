package androidx.core.net

import android.net.Uri
import java.io.File

fun Uri.toFile(): File {
  require(scheme == "file") { "Uri lacks 'file' scheme: $this" }
  val p = path ?: error("Uri path is null: $this")
  return File(if (Regex("^/[A-Za-z]:.*").matches(p)) p.drop(1) else p)
}
