package com.processout.processout_sdk.POWebViews;

import android.content.Context;
import android.net.Uri;

public class PaymentWebView extends ProcessOutWebView {

    public PaymentWebView(Context context) {
        super(context);
    }

    @Override
    void onRedirect(Uri uri) {
        // Configuring the webview
        String token = uri.getQueryParameter("token");

        if (token != null) {
            // Destroying the webview
            callback.onResult(token);
            return;
        }

        callback.onAuthenticationError();
    }
}
