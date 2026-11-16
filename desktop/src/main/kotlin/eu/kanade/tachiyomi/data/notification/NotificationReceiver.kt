package eu.kanade.tachiyomi.data.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga

/**
 * Desktop stand-in for upstream's NotificationReceiver. Windows balloon notifications carry no
 * action buttons, so the pending-intent factories return inert handles; the real actions
 * (pause downloads, mark as read, ...) are reachable from the app window instead.
 */
class NotificationReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {}

  companion object {
    internal fun resumeDownloadsPendingBroadcast(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun pauseDownloadsPendingBroadcast(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun clearDownloadsPendingBroadcast(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun dismissNotificationPendingBroadcast(context: Context, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun dismissNotification(context: Context, notificationId: Int, groupId: Int? = null) {}
    internal fun shareImagePendingBroadcast(context: Context, uri: Uri, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun deleteImagePendingBroadcast(context: Context, uri: Uri, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun openChapterPendingActivity(context: Context, manga: Manga, chapter: Chapter): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun openUpdatePendingActivity(context: Context, notes: String, downloadLink: String, releaseLink: String, version: String = ""): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun openChapterPendingActivity(context: Context, manga: Manga, groupId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun downloadChaptersPendingBroadcast(context: Context, manga: Manga, chapters: Array<Chapter>, groupId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun openErrorOrSkippedLogPendingActivity(context: Context, uri: Uri?): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun openFileExplorerPendingActivity(context: Context, uri: Uri): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun markAsReadPendingBroadcast(context: Context, manga: Manga, chapters: Array<Chapter>, groupId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun cancelLibraryUpdatePendingBroadcast(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun cancelTrackingSyncPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun cancelFollowSyncPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun startAppUpdatePendingJob(context: Context, url: String, version: String, notifyOnInstall: Boolean = false): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun shareBackupPendingBroadcast(context: Context, uri: Uri, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun cancelUpdateDownloadPendingBroadcast(context: Context): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun shareBackupPendingBroadcast(context: Context, uri: Uri, isLegacyFormat: Boolean, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun shareCrashLogPendingBroadcast(context: Context, uri: Uri, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
    internal fun cancelRestorePendingBroadcast(context: Context, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
  }
}
