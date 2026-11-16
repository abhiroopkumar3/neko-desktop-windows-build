package androidx.activity.result.contract

import android.net.Uri
import java.awt.FileDialog
import java.awt.Frame
import javax.swing.JFileChooser

/** Desktop contracts: each one opens the native Windows dialog instead of an Android intent. */
abstract class ActivityResultContract<I, O> {
  abstract fun launch(input: I): O
}

object ActivityResultContracts {
  private fun fileDialog(save: Boolean, title: String, name: String? = null): Uri? {
    val d = FileDialog(null as Frame?, title, if (save) FileDialog.SAVE else FileDialog.LOAD)
    name?.let { d.file = it }
    d.isVisible = true
    val f = d.file ?: return null
    return Uri.fromFile(java.io.File(d.directory, f))
  }

  class OpenDocumentTree : ActivityResultContract<Uri?, Uri?>() {
    override fun launch(input: Uri?): Uri? {
      val c = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY; dialogTitle = "Choose a folder" }
      return if (c.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) Uri.fromFile(c.selectedFile) else null
    }
  }
  class CreateDocument(private val mimeType: String = "*/*") : ActivityResultContract<String, Uri?>() {
    override fun launch(input: String): Uri? = fileDialog(true, "Save as", input)
  }
  class GetContent : ActivityResultContract<String, Uri?>() {
    override fun launch(input: String): Uri? = fileDialog(false, "Choose a file")
  }
  class OpenDocument : ActivityResultContract<Array<String>, Uri?>() {
    override fun launch(input: Array<String>): Uri? = fileDialog(false, "Choose a file")
  }
  class RequestPermission : ActivityResultContract<String, Boolean>() {
    override fun launch(input: String): Boolean = true
  }
  class RequestMultiplePermissions : ActivityResultContract<Array<String>, Map<String, Boolean>>() {
    override fun launch(input: Array<String>): Map<String, Boolean> = input.associateWith { true }
  }
  class StartActivityForResult : ActivityResultContract<android.content.Intent, Any?>() {
    override fun launch(input: android.content.Intent): Any? = null
  }
}
