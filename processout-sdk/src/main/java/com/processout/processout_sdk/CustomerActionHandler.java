package com.processout.processout_sdk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.processout.processout_sdk.POWebViews.ProcessOutWebView;
import com.processout.processout_sdk.POWebViews.WebViewCallback;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutException;
import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutWebException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

class CustomerActionHandler {
    private ThreeDSHandler handler;
    private Context with;
    private CustomerActionCallback callback;
    private Gson gson = new Gson();
    private ProcessOutWebView processOutWebView;

    private final String ThreeDS2ChallengeSuccess = "gway_req_eyJib2R5Ijoie1widHJhbnNTdGF0dXNcIjpcIllcIn0ifQ==";
    private final String ThreeDS2ChallengeError = "gway_req_eyJib2R5Ijoie1widHJhbnNTdGF0dXNcIjpcIk5cIn0ifQ==";

    public interface CustomerActionCallback {
        void shouldContinue(String source);
    }

    CustomerActionHandler(ThreeDSHandler handler, ProcessOutWebView processOutWebView, Context with, final CustomerActionCallback callback) {
        this.handler = handler;
        this.with = with;
        this.callback = callback;
        this.processOutWebView = processOutWebView;
    }

    void handleCustomerAction(CustomerAction customerAction) {
        switch (customerAction.getType()) {
            case FINGERPRINT_MOBILE:
                DirectoryServerData directoryServerData = gson.fromJson(
                        new String(Base64.decode(customerAction.getValue().getBytes(), Base64.NO_WRAP)), DirectoryServerData.class);
                handler.doFingerprint(directoryServerData, new ThreeDSHandler.DoFingerprintCallback() {
                    @Override
                    public void continueCallback(ThreeDSFingerprintResponse request) {
                        MiscGatewayRequest gwayRequest = new MiscGatewayRequest(gson.toJson(request, ThreeDSFingerprintResponse.class));
                        String jsonRequest = Base64.encodeToString(gson.toJson(gwayRequest, MiscGatewayRequest.class).getBytes(), Base64.NO_WRAP);
                        callback.shouldContinue("gway_req_" + jsonRequest);
                    }
                });
                break;
            case CHALLENGE_MOBILE:
                AuthenticationChallengeData authentificationData = gson.fromJson
                        (new String(Base64.decode(customerAction.getValue().getBytes(), Base64.NO_WRAP)), AuthenticationChallengeData.class);
                handler.doChallenge(authentificationData, new ThreeDSHandler.DoChallengeCallback() {
                    @Override
                    public void success() {
                        callback.shouldContinue(ThreeDS2ChallengeSuccess);
                    }

                    @Override
                    public void error() {
                        callback.shouldContinue(ThreeDS2ChallengeError);
                    }
                });
                break;
            case REDIRECT:
            case URL:
                processOutWebView.setCallback(new WebViewCallback() {
                    @Override
                    public void onResult(String token) {
                        callback.shouldContinue(token);
                    }

                    @Override
                    public void onAuthenticationError() {
                        handler.onError(new ProcessOutWebException("Web authentication failed"));
                    }
                });
                handler.doPresentWebView(processOutWebView);
                processOutWebView.loadUrl(customerAction.getValue());
                break;
            case FINGERPRINT:
                FrameLayout rootLayout = ((Activity) (with)).findViewById(android.R.id.content);

                // Building the possibly needed webView
                final WebView FingerprintWebView = new WebView(this.with);
                FingerprintWebView.getSettings().setUserAgentString("ProcessOut Android-Webview/" + ProcessOut.SDK_VERSION);
                FingerprintWebView.getSettings().setJavaScriptEnabled(true); // enable javascript

                // Defining fallback request in case the fingerprint times out or is unavailable
                final MiscGatewayRequest fallbackGwayRequest = new MiscGatewayRequest("{\"threeDS2FingerprintTimeout\":true}");
                fallbackGwayRequest.setURL(customerAction.getValue());
                HashMap<String, String> fallbackHeaders = new HashMap<String, String>();
                fallbackHeaders.put("Content-Type", "application/json");
                fallbackGwayRequest.setHeaders(fallbackHeaders);

                // Setup the timeout handler
                final Handler timeOutHandler = new android.os.Handler();
                // Configuring the action on timeout
                final Runnable fingerprintTimeoutClearer = new Runnable() {
                    @Override
                    public void run() {
                        // Destroying the webview
                        destroyWebView(FingerprintWebView);
                        callback.shouldContinue(fallbackGwayRequest.generateToken());
                    }
                };

                // Catch webview URL redirects
                FingerprintWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        // Check if the current Android version is supporte
                        String token = null;

                        // We cancel the timeout handler
                        timeOutHandler.removeCallbacks(fingerprintTimeoutClearer);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            token = request.getUrl().getQueryParameter("token");
                        }
                        if (token == null) {
                            // Android version not supported for fingerprinting
                            token = fallbackGwayRequest.generateToken();
                        }
                        callback.shouldContinue(token);
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                });

                // Start the timeout
                timeOutHandler.postDelayed(fingerprintTimeoutClearer, TimeUnit.SECONDS.toMillis(10));

                // Load the fingerprint URL
                FingerprintWebView.loadUrl(customerAction.getValue());

                // Add the webview to content
                if (with instanceof Activity) {
                    // We perform the fingerprint by displaying the hidden webview
                    rootLayout.addView(FingerprintWebView);
                } else {
                    // We can't instantiate the webview so fallback to default fingerprinting value
                    timeOutHandler.removeCallbacks(fingerprintTimeoutClearer);
                    callback.shouldContinue(fallbackGwayRequest.generateToken());
                }
                break;
            default:
                handler.onError(new ProcessOutException("Unhandled threeDS action:" + customerAction.getType().name()));
                break;
        }
    }

    private void destroyWebView(WebView webView) {
        // Preparing the webview removal
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.removeAllViews();
        webView.clearHistory();
        webView.clearCache(true);
        webView.onPause();
        webView.destroy();
    }
}
