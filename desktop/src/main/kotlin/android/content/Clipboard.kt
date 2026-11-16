package android.content

import android.net.Uri
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class ClipData private constructor(val label: CharSequence?, val text: CharSequence?, val uri: Uri?) {
  companion object {
    @JvmStatic fun newPlainText(label: CharSequence?, text: CharSequence?) = ClipData(label, text, null)
    @JvmStatic fun newRawUri(label: CharSequence?, uri: Uri?) = ClipData(label, null, uri)
  }
}

/** Windows clipboard through AWT. */
class ClipboardManager {
  fun setPrimaryClip(clip: ClipData) {
    val text = clip.text?.toString() ?: clip.uri?.toString() ?: return
    runCatching { Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null) }
  }
  val primaryClip: ClipData? get() = runCatching {
    (Toolkit.getDefaultToolkit().systemClipboard.getData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String)?.let { ClipData.newPlainText(null, it) }
  }.getOrNull()
}
