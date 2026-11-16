package android.app

class Notification(val channelId: String? = null, val title: CharSequence? = null, val text: CharSequence? = null, val contentIntent: PendingIntent? = null) {
  companion object {
    const val VISIBILITY_PUBLIC = 1
    const val VISIBILITY_PRIVATE = 0
    const val VISIBILITY_SECRET = -1
    const val FLAG_ONGOING_EVENT = 2
    const val DEFAULT_ALL = -1
    const val PRIORITY_LOW = -1
    const val PRIORITY_DEFAULT = 0
    const val PRIORITY_HIGH = 1
    const val CATEGORY_PROGRESS = "progress"
    const val CATEGORY_ERROR = "err"
    const val CATEGORY_SERVICE = "service"
  }
}
