package com.processout.processout_sdk;

import org.json.JSONObject;

interface RequestAuthorizationCallback {
    void onError(Exception error);

    void onSuccess(JSONObject json);
}
