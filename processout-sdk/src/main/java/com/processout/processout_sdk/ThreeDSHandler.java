package com.processout.processout_sdk;

import java.util.Map;

public interface ThreeDSHandler {

    interface DoFingerprintCallback {
        void continueCallback(ThreeDSFingerprintResponse request);
    }

    void doFingerprint(Map<String, String> directoryServerData, DoFingerprintCallback callback);

    interface DoChallengeCallback {
        void success();
        void error();
    }

    void doChallenge(ThreeDSGatewayRequest authentificationData, DoChallengeCallback callback);

    void onSuccess(String invoiceId);

    void onError(Exception error);
}
