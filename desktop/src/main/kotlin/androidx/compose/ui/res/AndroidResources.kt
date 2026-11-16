package androidx.compose.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import android.content.DesktopContext
import org.nekomanga.desktop.res.DrawableLoader

/** Id-based resource helpers (Android API) on top of the desktop Resources implementation. */
@Composable fun stringResource(id: Int): String = DesktopContext.resources.getString(id)

@Composable
fun stringResource(id: Int, vararg formatArgs: Any): String =
    DesktopContext.resources.getString(id, *formatArgs)

@Composable
fun pluralStringResource(id: Int, count: Int): String =
    DesktopContext.resources.getQuantityString(id, count)

@Composable
fun pluralStringResource(id: Int, count: Int, vararg formatArgs: Any): String =
    DesktopContext.resources.getQuantityString(id, count, *formatArgs)

@Composable fun stringArrayResource(id: Int): Array<String> = DesktopContext.resources.getStringArray(id)

@Composable fun colorResource(id: Int): Color = Color(DesktopContext.resources.getColor(id))

/** Loads upstream's vector XML (or PNG) drawables; an unreadable one draws nothing and is logged. */
@Composable
fun painterResource(id: Int): Painter {
  val density = LocalDensity.current
  val vector = remember(id, density) { DrawableLoader.vector(id, density) }
  if (vector != null) return rememberVectorPainter(vector)
  val bitmap = remember(id) { DrawableLoader.bitmap(id) }
  return if (bitmap != null) androidx.compose.ui.graphics.painter.BitmapPainter(bitmap) else ColorPainter(Color.Transparent)
}
