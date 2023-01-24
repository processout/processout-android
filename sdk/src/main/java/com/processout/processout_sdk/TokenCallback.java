package com.processout.processout_sdk;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public interface TokenCallback {
    void onError(Exception error);

    void onSuccess(String token);
}