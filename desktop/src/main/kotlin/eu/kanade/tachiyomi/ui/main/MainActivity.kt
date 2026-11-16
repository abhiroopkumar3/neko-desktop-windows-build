package eu.kanade.tachiyomi.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

/**
 * Desktop stand-in for the Android main activity. The window itself lives in
 * org.nekomanga.desktop (Main.kt); this keeps the deep-link intent builders upstream code calls.
 */
class MainActivity : AppCompatActivity() {
  companion object {
    fun openMangaIntent(context: Context, id: Long?, canReturnToMain: Boolean = false) =
        Intent(context, MainActivity::class.java).apply {
          action = if (canReturnToMain) DeepLinks.Actions.MangaBack else DeepLinks.Actions.Manga
          putExtra(DeepLinks.Extras.MangaId, id)
        }

    fun openReaderSettings(context: Context) =
        Intent(context, MainActivity::class.java).apply { action = DeepLinks.Actions.ReaderSettings }
  }
}
