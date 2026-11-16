package com.google.firebase.analytics

class FirebaseAnalytics private constructor() {
  fun setAnalyticsCollectionEnabled(v: Boolean) {}
  fun logEvent(name: String, params: android.os.Bundle?) {}
  companion object { private val i = FirebaseAnalytics(); @JvmStatic fun getInstance(context: android.content.Context) = i }
}
