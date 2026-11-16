package android.content

import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/** File-backed content resolver: only file URIs exist on desktop. */
object ContentResolver {
  const val SCHEME_CONTENT = "content"
  const val SCHEME_FILE = "file"
  const val SCHEME_ANDROID_RESOURCE = "android.resource"
  private fun file(uri: Uri) = File(uri.path.orEmpty().let { if (Regex("^/[A-Za-z]:.*").matches(it)) it.drop(1) else it })
  fun openInputStream(uri: Uri): InputStream? = runCatching { file(uri).inputStream() }.getOrNull()
  fun openOutputStream(uri: Uri): OutputStream? = runCatching { file(uri).outputStream() }.getOrNull()
  fun openOutputStream(uri: Uri, mode: String): OutputStream? = runCatching { java.io.FileOutputStream(file(uri), mode.contains('a')) }.getOrNull()
  fun takePersistableUriPermission(uri: Uri, flags: Int) {}
  fun getType(uri: Uri): String? = java.net.URLConnection.guessContentTypeFromName(file(uri).name)
}
