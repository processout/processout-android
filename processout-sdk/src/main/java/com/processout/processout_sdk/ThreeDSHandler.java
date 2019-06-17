package com.processout.processout_sdk;

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

    void onSuccess(String invoiceId);

    void onError(Exception error);
}
