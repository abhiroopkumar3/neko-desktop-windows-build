package android.webkit;

import android.graphics.Bitmap;

public class WebChromeClient {
    public void onProgressChanged(WebView view, int newProgress) {}
    public void onReceivedTitle(WebView view, String title) {}
    public void onReceivedIcon(WebView view, Bitmap icon) {}
}
