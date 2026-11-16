package android.app

import android.content.Context
import android.content.Intent
import android.view.Window

/** Screens are Compose destinations on desktop; this base only keeps View-era type references compiling. */
open class Activity : android.content.ContextWrapper(null), androidx.lifecycle.LifecycleOwner {
  override val lifecycle: androidx.lifecycle.Lifecycle = androidx.lifecycle.LifecycleRegistry(this).also { it.currentState = androidx.lifecycle.Lifecycle.State.RESUMED }
  open fun finishAffinity() {}
  var composeContent: (@androidx.compose.runtime.Composable () -> Unit)? = null
  open fun recreate() {}
  open fun finishAfterTransition() {}
  open var intent: Intent = Intent()
  open var title: CharSequence = ""
  val window: Window = Window()
  open fun finish() {}
  open fun setResult(code: Int) {}
  open fun setResult(code: Int, data: Intent?) {}
  open fun overridePendingTransition(enter: Int, exit: Int) {}
  open fun overrideActivityTransition(type: Int, enter: Int, exit: Int) {}
  open fun onCreate(savedInstanceState: android.os.Bundle?) {}
  open fun onResume() {}
  open fun onPause() {}
  open fun onDestroy() {}
  open fun onNewIntent(intent: Intent) {}
  companion object { const val RESULT_OK = -1; const val RESULT_CANCELED = 0; const val OVERRIDE_TRANSITION_OPEN = 0; const val OVERRIDE_TRANSITION_CLOSE = 1 }
}
