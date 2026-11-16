package com.google.android.material.dialog

import android.content.Context

/** View-era dialog builder; desktop screens use Compose dialogs, so this only keeps the upstream references compiling. */
class MaterialAlertDialogBuilder(val context: Context) {
  fun setTitle(t: CharSequence?) = this
  fun setTitle(res: Int) = this
  fun setMessage(t: CharSequence?) = this
  fun setMessage(res: Int) = this
  fun setPositiveButton(t: CharSequence?, l: Any?) = this
  fun setPositiveButton(res: Int, l: Any?) = this
  fun setNegativeButton(t: CharSequence?, l: Any?) = this
  fun setNegativeButton(res: Int, l: Any?) = this
  fun show(): Any? = null
}
