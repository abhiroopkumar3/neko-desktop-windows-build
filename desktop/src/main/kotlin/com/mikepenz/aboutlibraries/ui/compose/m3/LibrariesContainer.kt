package com.mikepenz.aboutlibraries.ui.compose.m3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.Libraries

@Composable
fun LibrariesContainer(modifier: Modifier = Modifier, libraries: Libraries? = null, contentPadding: PaddingValues = PaddingValues()) {
  Text(
      "Neko is Apache-2.0 licensed. Full third-party notices are in THIRD-PARTY-NOTICES.txt next to the app.",
      modifier = modifier.padding(contentPadding).padding(16.dp),
  )
}
