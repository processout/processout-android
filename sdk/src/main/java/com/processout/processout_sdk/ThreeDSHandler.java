package com.processout.processout_sdk;

import com.processout.processout_sdk.POWebViews.ProcessOutWebView;

public interface ThreeDSHandler {

    interface DoFingerprintCallback {
        void continueCallback(ThreeDSFingerprintResponse request);
    }

    void doFingerprint(DirectoryServerData directoryServerData, DoFingerprintCallback callback);

    interface DoChallengeCallback {
        void success();

        void error();
    }

    void doChallenge(AuthenticationChallengeData authentificationData, DoChallengeCallback callback);

    void doPresentWebView(ProcessOutWebView webView);

    void onSuccess(String id);

    void onError(Exception error);
}
