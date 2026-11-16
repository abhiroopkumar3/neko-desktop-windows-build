package org.nekomanga.desktop

import android.content.DesktopContext
import coil3.SingletonImageLoader
import eu.kanade.tachiyomi.AppModule
import eu.kanade.tachiyomi.PreferenceModule
import eu.kanade.tachiyomi.data.coil.coilImageLoader
import org.nekomanga.logging.TimberKt
import timber.log.Timber
import uy.kohesive.injekt.Injekt

/** What Android does in Application.onCreate: dependency injection, logging and the image loader. */
object DesktopStartup {
  @Volatile private var started = false

  @Synchronized
  fun start() {
    if (started) return
    started = true
    DesktopContext.onCreate()
    Injekt.importModule(PreferenceModule(DesktopContext))
    Injekt.importModule(AppModule(DesktopContext))
    TimberKt.plant(Timber.DebugTree())
    SingletonImageLoader.setSafe { coilImageLoader(DesktopContext) }
  }
}
