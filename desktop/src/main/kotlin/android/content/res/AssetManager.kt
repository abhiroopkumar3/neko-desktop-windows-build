package android.content.res

import java.io.InputStream

/** App assets are bundled into the jar under android-assets/. */
object AssetManager {
  fun open(name: String): InputStream = AssetManager::class.java.getResourceAsStream("/android-assets/$name") ?: throw java.io.FileNotFoundException(name)
}
