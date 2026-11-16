package android.webkit

import android.content.Context
import android.net.Uri
import android.view.View
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

class WebSettings {
  var userAgentString: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"
  var javaScriptEnabled = false
  var domStorageEnabled = false
  var blockNetworkImage = false
  var allowFileAccess = false
  var allowContentAccess = false
  var loadWithOverviewMode = false
  var useWideViewPort = false
  var mixedContentMode = 0
  var cacheMode = 0
  var databaseEnabled = false
  var mediaPlaybackRequiresUserGesture = true
  var javaScriptCanOpenWindowsAutomatically = false
  var setSupportZoom = true
  var builtInZoomControls = false
  var displayZoomControls = true
  companion object { const val LOAD_DEFAULT = -1; const val LOAD_NO_CACHE = 2; const val MIXED_CONTENT_ALWAYS_ALLOW = 0 }
}

interface WebResourceRequest {
  val url: Uri
  val method: String get() = "GET"
  val requestHeaders: Map<String, String> get() = emptyMap()
  val isForMainFrame: Boolean get() = true
}

class WebResourceError { val errorCode: Int = 0; val description: CharSequence = "" }

class WebResourceResponse(
    var mimeType: String?, var encoding: String?, var statusCode: Int, var reasonPhrase: String?,
    var responseHeaders: Map<String, String>?, var data: InputStream?,
) {
  constructor(mimeType: String?, encoding: String?, data: InputStream?) : this(mimeType, encoding, 200, "OK", null, data)
}

/**
 * Placeholder until the embedded Chromium browser (JCEF) is wired in: it accepts the same calls
 * but loads nothing. Sources that depend on page scripts report failure instead of hanging.
 */
open class WebView(val context: Context? = null) : View(context) {
  val settings = WebSettings()
  var webViewClient: WebViewClient = WebViewClient()
  var webChromeClient: WebChromeClient? = null
  var url: String? = null
    private set
  val title: String? get() = null
  fun loadUrl(url: String) { this.url = url }
  fun loadUrl(url: String, headers: Map<String, String>) { this.url = url }
  fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, historyUrl: String?) { this.url = baseUrl }
  fun evaluateJavascript(script: String, callback: ValueCallback<String>?) { callback?.onReceiveValue("null") }
  fun addJavascriptInterface(obj: Any, name: String) {}
  fun removeJavascriptInterface(name: String) {}
  fun stopLoading() {}
  fun reload() {}
  fun goBack() {}
  fun goForward() {}
  fun canGoBack() = false
  fun canGoForward() = false
  fun destroy() {}
  fun clearCache(includeDiskFiles: Boolean) {}
  fun clearHistory() {}
  fun clearFormData() {}
  fun clearSslPreferences() {}
  companion object {
    @JvmStatic fun setWebContentsDebuggingEnabled(enabled: Boolean) {}
    @JvmStatic fun getCurrentWebViewPackage(): Any? = null
  }
}

fun interface ValueCallback<T> { fun onReceiveValue(value: T) }

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class JavascriptInterface

/** Process-wide cookie jar (host -> "name=value; ..."); the browser port keeps it in sync with Chromium. */
class CookieManager private constructor() {
  private val jar = ConcurrentHashMap<String, MutableMap<String, String>>()
  private fun host(url: String) = runCatching { java.net.URI(url).host }.getOrNull() ?: url
  fun getCookie(url: String): String? = jar[host(url)]?.entries?.joinToString("; ") { "${it.key}=${it.value}" }?.takeIf { it.isNotEmpty() }
  fun setCookie(url: String, value: String) {
    val pair = value.substringBefore(';').split('=', limit = 2)
    if (pair.size == 2) jar.getOrPut(host(url)) { ConcurrentHashMap() }[pair[0].trim()] = pair[1].trim()
  }
  fun setCookie(url: String, value: String, callback: ValueCallback<Boolean>?) { setCookie(url, value); callback?.onReceiveValue(true) }
  fun removeAllCookies(callback: ValueCallback<Boolean>?) { jar.clear(); callback?.onReceiveValue(true) }
  fun removeAllCookie() { jar.clear() }
  fun hasCookies() = jar.isNotEmpty()
  fun flush() {}
  fun setAcceptCookie(v: Boolean) {}
  fun setAcceptThirdPartyCookies(view: WebView?, v: Boolean) {}
  companion object { private val instance = CookieManager(); @JvmStatic fun getInstance() = instance }
}

class WebStorage private constructor() {
  fun deleteAllData() {}
  companion object { private val instance = WebStorage(); @JvmStatic fun getInstance() = instance }
}

class MimeTypeMap private constructor() {
  fun getMimeTypeFromExtension(ext: String): String? = java.net.URLConnection.guessContentTypeFromName("x.$ext")
  fun getExtensionFromMimeType(mime: String): String? = when (mime.lowercase()) {
    "image/jpeg" -> "jpg"; "image/png" -> "png"; "image/gif" -> "gif"; "image/webp" -> "webp"; "application/zip" -> "zip"
    else -> mime.substringAfter('/', "").takeIf { it.isNotEmpty() }
  }
  companion object {
    private val instance = MimeTypeMap()
    @JvmStatic fun getSingleton() = instance
    @JvmStatic fun getFileExtensionFromUrl(url: String): String = url.substringBefore('?').substringAfterLast('.', "")
  }
}
