package android.app

import android.content.Context

/** Base class of the app's Application object; the desktop entry point creates and starts it. */
open class Application : Context() {
  interface ActivityLifecycleCallbacks {
    fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {}
    fun onActivityStarted(activity: Activity) {}
    fun onActivityResumed(activity: Activity) {}
    fun onActivityPaused(activity: Activity) {}
    fun onActivityStopped(activity: Activity) {}
    fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) {}
    fun onActivityDestroyed(activity: Activity) {}
  }
  fun registerActivityLifecycleCallbacks(cb: ActivityLifecycleCallbacks) {}
  fun unregisterActivityLifecycleCallbacks(cb: ActivityLifecycleCallbacks) {}
  fun getProcessName(): String = "neko"
  open fun onCreate() {}
  open fun onTerminate() {}
}
