package com.processout.processout_sdk;

public interface ThreeDSVerificationCallback {
    public void onSuccess(String invoiceId);
    public void onError(Exception error);
}
