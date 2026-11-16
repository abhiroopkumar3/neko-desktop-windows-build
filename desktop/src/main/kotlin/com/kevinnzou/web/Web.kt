package com.kevinnzou.web

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class LoadingState {
  object Initializing : LoadingState()
  data class Loading(val progress: Float) : LoadingState()
  object Finished : LoadingState()
}

@Stable
class WebViewState(val url: String, val additionalHttpHeaders: Map<String, String> = emptyMap()) {
  var loadingState: LoadingState by mutableStateOf(LoadingState.Initializing)
  var lastLoadedUrl: String? by mutableStateOf(url)
  var pageTitle: String? by mutableStateOf(null)
}

@Stable
class WebViewNavigator {
  var canGoBack by mutableStateOf(false)
  var canGoForward by mutableStateOf(false)
  fun navigateBack() {}
  fun navigateForward() {}
  fun reload() {}
  fun stopLoading() {}
  fun loadUrl(url: String, headers: Map<String, String> = emptyMap()) {}
}

@Composable
fun rememberWebViewState(url: String, additionalHttpHeaders: Map<String, String> = emptyMap()): WebViewState = remember(url) { WebViewState(url, additionalHttpHeaders) }

@Composable fun rememberWebViewNavigator(): WebViewNavigator = remember { WebViewNavigator() }

/** Embedded browser placeholder until JCEF is integrated. */
@Composable
fun WebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    onCreated: (WebView) -> Unit = {},
    client: AccompanistWebViewClient = AccompanistWebViewClient(),
) {
  remember { onCreated(WebView()); 0 }
  Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("The embedded browser is not part of this build yet.") }
}
