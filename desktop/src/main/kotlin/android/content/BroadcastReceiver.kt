package android.content

abstract class BroadcastReceiver {
  abstract fun onReceive(context: Context, intent: Intent)
  inner class PendingResult { fun finish() {} }
  fun goAsync(): PendingResult = PendingResult()
}

class IntentFilter(val action: String? = null) {
  fun addAction(a: String) {}
}
