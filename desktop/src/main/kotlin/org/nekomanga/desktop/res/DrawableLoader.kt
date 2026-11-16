package org.nekomanga.desktop.res

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.Density
import java.util.concurrent.ConcurrentHashMap
import org.xml.sax.InputSource

/** Resolves R.drawable / R.mipmap ids to the bundled upstream files. */
object DrawableLoader {
  // Preference order approximates Android picking the best density for a large desktop screen.
  private val qualifierOrder = listOf("", "-anydpi", "-anydpi-v26", "-v24", "-v26", "-v28", "-nodpi", "-xxxhdpi", "-xxhdpi", "-xhdpi", "-hdpi", "-mdpi")
  private val missing = ConcurrentHashMap.newKeySet<Int>()

  private fun nameOf(id: Int): Pair<String, String>? = when {
    id >= RTable.MIPMAP_BASE && id - RTable.MIPMAP_BASE < RTable.mipmap.size -> "mipmap" to RTable.mipmap[id - RTable.MIPMAP_BASE]
    id >= RTable.DRAWABLE_BASE && id - RTable.DRAWABLE_BASE < RTable.drawable.size -> "drawable" to RTable.drawable[id - RTable.DRAWABLE_BASE]
    else -> null
  }

  private fun open(id: Int, extensions: List<String>): Pair<java.io.InputStream, String>? {
    val (kind, name) = nameOf(id) ?: return null
    for (q in qualifierOrder) for (ext in extensions) {
      val path = "/android-res/img/$kind$q/$name.$ext"
      DrawableLoader::class.java.getResourceAsStream(path)?.let { return it to ext }
    }
    if (missing.add(id)) System.err.println("Drawable not bundled: $kind/$name")
    return null
  }

  fun vector(id: Int, density: Density): ImageVector? {
    val (stream, _) = open(id, listOf("xml")) ?: return null
    return runCatching { stream.use { loadXmlImageVector(InputSource(it), density) } }
        .onFailure { if (missing.add(id)) System.err.println("Cannot read vector drawable ${nameOf(id)}: ${it.message}") }
        .getOrNull()
  }

  fun bitmapOrNull(id: Int): android.graphics.Bitmap? {
    val (stream, _) = open(id, listOf("png", "webp", "jpg")) ?: return null
    return runCatching { stream.use { javax.imageio.ImageIO.read(it) }?.let { android.graphics.Bitmap(it) } }.getOrNull()
  }

  fun bitmap(id: Int): ImageBitmap? {
    val (stream, _) = open(id, listOf("png", "webp", "jpg")) ?: return null
    return runCatching { stream.use { loadImageBitmap(it) } }.getOrNull()
  }
}
