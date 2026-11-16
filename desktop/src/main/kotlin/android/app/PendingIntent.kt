package android.app

import android.content.Context
import android.content.Intent

class PendingIntent private constructor(val intent: Intent, val broadcast: Boolean) {
  companion object {
    const val FLAG_ONE_SHOT = 0x40000000
    const val FLAG_CANCEL_CURRENT = 0x10000000
    const val FLAG_UPDATE_CURRENT = 0x08000000
    const val FLAG_IMMUTABLE = 0x04000000
    const val FLAG_MUTABLE = 0x02000000
    @JvmStatic fun getActivity(context: Context?, requestCode: Int, intent: Intent, flags: Int) = PendingIntent(intent, false)
    @JvmStatic fun getBroadcast(context: Context?, requestCode: Int, intent: Intent, flags: Int) = PendingIntent(intent, true)
  }
}
