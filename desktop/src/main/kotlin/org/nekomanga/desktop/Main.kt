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
          Text("Neko Desktop", style = MaterialTheme.typography.headlineLarge)
          Text("Windows port skeleton", style = MaterialTheme.typography.bodyLarge)
        }
      }
    }
  }
}
