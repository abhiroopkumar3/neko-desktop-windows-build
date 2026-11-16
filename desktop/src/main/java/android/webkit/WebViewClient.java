package android.webkit;

import android.graphics.Bitmap;

/** Callback base class; Java so overrides may declare their parameters nullable or not, like on Android. */
public class WebViewClient {
    public void onPageStarted(WebView view, String url, Bitmap favicon) {}
    public void onPageFinished(WebView view, String url) {}
    public void onLoadResource(WebView view, String url) {}
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {}
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) { return null; }
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) { return false; }
    public boolean shouldOverrideUrlLoading(WebView view, String url) { return false; }
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {}
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {}
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {}
}
