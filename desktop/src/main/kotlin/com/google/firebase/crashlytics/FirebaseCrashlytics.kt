package com.google.firebase.crashlytics

/** Crash reporting stays local on desktop: calls are accepted and dropped. */
class FirebaseCrashlytics private constructor() {
  fun recordException(t: Throwable) {}
  fun log(msg: String) {}
  fun setCustomKey(k: String, v: String) {}
  fun sendUnsentReports() {}
  var isCrashlyticsCollectionEnabled: Boolean = false
  companion object { private val i = FirebaseCrashlytics(); @JvmStatic fun getInstance() = i }
}
