package android.app

class NotificationChannelGroup(val id: String, val name: CharSequence)

class NotificationChannel(val id: String, val name: CharSequence, val importance: Int) {
  var description: String? = null
  var group: String? = null
  var lockscreenVisibility: Int = 0
  fun setShowBadge(v: Boolean) {}
  fun setSound(uri: Any?, attrs: Any?) {}
  fun enableVibration(v: Boolean) {}
  fun enableLights(v: Boolean) {}
  fun setVibrationPattern(p: LongArray?) {}
}

class NotificationManager {
  private val channels = LinkedHashMap<String, NotificationChannel>()
  fun createNotificationChannel(c: NotificationChannel) { channels[c.id] = c }
  fun createNotificationChannels(list: List<NotificationChannel>) { list.forEach(::createNotificationChannel) }
  fun createNotificationChannelGroup(g: NotificationChannelGroup) {}
  fun createNotificationChannelGroups(list: List<NotificationChannelGroup>) {}
  fun getNotificationChannel(id: String): NotificationChannel? = channels[id]
  fun deleteNotificationChannel(id: String) { channels.remove(id) }
  fun cancel(id: Int) {}
  fun cancelAll() {}
  companion object {
    const val IMPORTANCE_NONE = 0
    const val IMPORTANCE_LOW = 2
    const val IMPORTANCE_DEFAULT = 3
    const val IMPORTANCE_HIGH = 4
  }
}
