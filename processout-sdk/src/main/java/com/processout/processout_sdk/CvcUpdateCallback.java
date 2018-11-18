package com.processout.processout_sdk;

/**
 * Created by jeremylejoux on 1/18/18.
 */

public interface CvcUpdateCallback {
    public void onSuccess();
    public void onError(Exception error);
}
