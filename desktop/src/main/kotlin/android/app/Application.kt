package android.app

import android.content.Context

/** Base class of the app's Application object; the desktop entry point creates and starts it. */
open class Application : Context() {
  open fun onCreate() {}
  open fun onTerminate() {}
}
