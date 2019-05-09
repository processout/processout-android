package com.processout.processout_sdk;

import java.util.ArrayList;

public interface ListAlternativeMethodsCallback {
    void onSuccess(ArrayList<AlternativeGateway> gateways);
    void onError(Exception e);
}
