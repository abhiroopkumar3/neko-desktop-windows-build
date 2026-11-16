package eu.kanade.tachiyomi.ui.reader

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga

/** Desktop stand-in: the reader is a Compose window opened through ReaderLauncher, not an activity. */
class ReaderActivity : AppCompatActivity() {
  companion object {
    fun newIntent(context: Context, manga: Manga, chapter: Chapter): Intent = newIntent(context, manga.id, chapter.id)

    fun newIntent(context: Context, mangaId: Long?, chapterId: Long?): Intent =
        Intent(context, ReaderActivity::class.java).apply {
          putExtra("manga", mangaId)
          putExtra("chapter", chapterId)
          addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
  }
}
