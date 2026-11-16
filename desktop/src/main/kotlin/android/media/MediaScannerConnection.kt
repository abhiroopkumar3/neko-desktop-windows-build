package android.media

object MediaScannerConnection {
  fun interface OnScanCompletedListener { fun onScanCompleted(path: String?, uri: android.net.Uri?) }
  @JvmStatic fun scanFile(context: android.content.Context, paths: Array<String>, mimeTypes: Array<String>?, cb: OnScanCompletedListener?) {}
}
