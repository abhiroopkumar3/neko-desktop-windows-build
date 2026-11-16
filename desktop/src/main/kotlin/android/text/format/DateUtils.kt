package android.text.format

object DateUtils {
  const val SECOND_IN_MILLIS = 1000L
  const val MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS
  const val HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS
  const val DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS
  const val WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS

  @JvmStatic
  fun getRelativeTimeSpanString(time: Long): CharSequence = getRelativeTimeSpanString(time, System.currentTimeMillis(), MINUTE_IN_MILLIS)

  @JvmStatic
  fun getRelativeTimeSpanString(time: Long, now: Long, minResolution: Long): CharSequence {
    val diff = kotlin.math.abs(now - time)
    val past = now >= time
    fun fmt(n: Long, unit: String) = if (past) "$n $unit${if (n == 1L) "" else "s"} ago" else "in $n $unit${if (n == 1L) "" else "s"}"
    return when {
      diff < MINUTE_IN_MILLIS && minResolution < MINUTE_IN_MILLIS -> fmt(diff / SECOND_IN_MILLIS, "second")
      diff < HOUR_IN_MILLIS && minResolution < HOUR_IN_MILLIS -> fmt(diff / MINUTE_IN_MILLIS, "minute")
      diff < DAY_IN_MILLIS && minResolution < DAY_IN_MILLIS -> fmt(diff / HOUR_IN_MILLIS, "hour")
      diff < WEEK_IN_MILLIS -> if (diff / DAY_IN_MILLIS == 0L) "Today" else fmt(diff / DAY_IN_MILLIS, "day")
      else -> java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(java.util.Date(time))
    }
  }
}
