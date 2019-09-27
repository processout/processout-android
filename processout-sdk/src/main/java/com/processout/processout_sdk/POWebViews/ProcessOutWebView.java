package com.processout.processout_sdk.POWebViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.processout.processout_sdk.ProcessOut;

public abstract class ProcessOutWebView extends WebView {

    final private String REDIRECT_URL_PATTERN = "https:\\/\\/checkout\\.processout\\.(ninja|com)\\/helpers\\/mobile-processout-webview-landing.*";
    protected WebViewCallback callback;

    public ProcessOutWebView(Context context) {
        super(context);
        this.getSettings().setUserAgentString("ProcessOut Android-Webview/" + ProcessOut.SDK_VERSION);
        this.getSettings().setJavaScriptEnabled(true); // enable javascript

        // Catch URL loading
        final ProcessOutWebView instance = this;
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.matches(REDIRECT_URL_PATTERN))
                    instance.onRedirect(Uri.parse(url));
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    public void setCallback(WebViewCallback callback) {
        this.callback = callback;
    }

    abstract void onRedirect(Uri uri);
}
