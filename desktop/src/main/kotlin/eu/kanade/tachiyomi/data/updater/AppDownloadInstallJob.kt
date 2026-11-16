package eu.kanade.tachiyomi.data.updater

import android.content.Context
import org.nekomanga.BuildConfig

/** Entry point kept for upstream callers; the Windows updater replaces the body in the packaging phase. */
object AppDownloadInstallJob {
  const val PACKAGE_INSTALLED_ACTION = "${BuildConfig.APPLICATION_ID}.SESSION_SELF_API_PACKAGE_INSTALLED"
  internal const val EXTRA_FILE_URI = "${BuildConfig.APPLICATION_ID}.AppInstaller.FILE_URI"
  internal const val EXTRA_NOTIFY_ON_INSTALL = "ACTION_ON_INSTALL"
  internal const val EXTRA_DOWNLOAD_URL = "DOWNLOAD_URL"
  internal const val EXTRA_VERSION = "DOWNLOAD_VERSION"
  internal const val NOTIFY_ON_INSTALL_KEY = "notify_on_install_complete"
  internal const val DOWNLOADING_VERSION_KEY = "downloading_version"
  const val ALWAYS = 0
  const val ONLY_ON_UNMETERED = 1
  const val NEVER = 2

  suspend fun start(context: Context, url: String?, notifyOnInstall: Boolean, waitUntilIdle: Boolean = false, version: String) {}

  fun stop(context: Context) {}
}
