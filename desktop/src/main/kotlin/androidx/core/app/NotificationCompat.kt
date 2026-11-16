package androidx.core.app

import android.app.PendingIntent
import android.app.Notification
import android.content.Context

/**
 * Collects what upstream asks to show; the desktop notification service (tray/toast) reads
 * [Notification] objects. Builder methods are chainable like the Android class.
 */

object NotificationCompat {
  const val PRIORITY_MIN = -2
  const val PRIORITY_LOW = -1
  const val PRIORITY_DEFAULT = 0
  const val PRIORITY_HIGH = 1
  const val GROUP_ALERT_SUMMARY = 1
  const val CATEGORY_ERROR = "err"
  const val CATEGORY_PROGRESS = "progress"

  open class Style
  class BigTextStyle : Style() {
    var text: CharSequence? = null
    fun bigText(t: CharSequence?) = apply { text = t }
    fun setBigContentTitle(t: CharSequence?) = this
  }
  class BigPictureStyle : Style() {
    fun bigPicture(b: Any?) = this
    fun setSummaryText(t: CharSequence?) = this
  }

  class Builder(val context: Context?, val channelId: String?) {
    private var title: CharSequence? = null
    private var text: CharSequence? = null
    private var content: PendingIntent? = null
    fun setContentTitle(t: CharSequence?) = apply { title = t }
    fun setContentText(t: CharSequence?) = apply { text = t }
    fun setSmallIcon(id: Int) = this
    fun setLargeIcon(b: Any?) = this
    fun setAutoCancel(v: Boolean) = this
    fun setOngoing(v: Boolean) = this
    fun setOnlyAlertOnce(v: Boolean) = this
    fun setPriority(p: Int) = this
    fun setColor(c: Int) = this
    fun setCategory(c: String?) = this
    fun setGroup(g: String?) = this
    fun setGroupSummary(v: Boolean) = this
    fun setGroupAlertBehavior(v: Int) = this
    fun setProgress(max: Int, progress: Int, indeterminate: Boolean) = this
    fun setStyle(s: Style?) = this
    fun setContentIntent(i: PendingIntent?) = apply { content = i }
    fun setDeleteIntent(i: PendingIntent?) = this
    fun setSubText(t: CharSequence?) = this
    fun setShowWhen(v: Boolean) = this
    fun setTicker(t: CharSequence?) = this
    fun addAction(icon: Int, title: CharSequence?, intent: PendingIntent?) = this
    fun clearActions() = this
    fun build(): Notification = Notification(channelId, title, text, content)
  }
}

class NotificationManagerCompat private constructor() {
  fun notify(id: Int, n: Notification) { org.nekomanga.desktop.platform.Toasts.show((n.title ?: n.text ?: "").toString()) }
  fun cancel(id: Int) {}
  fun cancelAll() {}
  companion object { @JvmStatic fun from(context: Context?) = NotificationManagerCompat() }
}
