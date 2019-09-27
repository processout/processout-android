package com.processout.processout_sdk.POWebViews;

public interface WebViewCallback {
    void onResult(String token);

    void onAuthenticationError();
}
