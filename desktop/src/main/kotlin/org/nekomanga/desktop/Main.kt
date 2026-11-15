package org.nekomanga.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import android.content.DesktopContext
import org.nekomanga.BuildConfig
import org.nekomanga.R

fun main() = application {
  Window(
      onCloseRequest = ::exitApplication,
      title = "Neko",
      state = rememberWindowState(size = DpSize(1100.dp, 750.dp)),
  ) {
    MaterialTheme {
      Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Strings come from upstream's own strings.xml through the Android resource shim.
          Text(DesktopContext.getString(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
          Text("Desktop port ${BuildConfig.VERSION_NAME} (shared core build)", style = MaterialTheme.typography.bodyLarge)
          Text(DesktopContext.getString(R.string.library), style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
  }
}
