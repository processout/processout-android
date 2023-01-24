package com.processout.processout_sdk.POWebViews;

import android.content.Context;
import android.net.Uri;

public class CardTokenWebView extends ProcessOutWebView {

    public CardTokenWebView(Context context) {
        super(context);
    }

    @Override
    void onRedirect(Uri uri) {
        // Configuring the webview
        String token = uri.getQueryParameter("token");
        if (token != null) {
            // Destroying the webview
            callback.onResult(token);
        }
    }
}
