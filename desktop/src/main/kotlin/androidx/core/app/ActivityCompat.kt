package androidx.core.app

import android.app.Activity

/** Windows has no runtime permission prompts: everything is granted. */
object ActivityCompat {
  @JvmStatic fun recreate(activity: Activity) {}
  @JvmStatic fun checkSelfPermission(context: android.content.Context, permission: String): Int = 0
  @JvmStatic fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {}
  @JvmStatic fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean = false
}
