package com.processout.processout_sdk;

import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutException;

public interface ThreeDSVerificationCallback {
    public void onSuccess(String invoiceId);
    public void onError(Exception error);
}
