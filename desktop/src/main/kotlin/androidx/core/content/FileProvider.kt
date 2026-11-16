package androidx.core.content

import android.content.Context
import android.net.Uri
import java.io.File

/** Files are shared by path on desktop, so no content:// indirection is needed. */
open class FileProvider {
  companion object { @JvmStatic fun getUriForFile(context: Context, authority: String, file: File): Uri = Uri.fromFile(file) }
}
