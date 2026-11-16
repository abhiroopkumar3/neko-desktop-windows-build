package eu.kanade.tachiyomi.util.manga

/** Android launcher shortcuts do not exist on Windows; the call is accepted and ignored. */
class MangaShortcutManager {
  suspend fun updateShortcuts() {}
}
