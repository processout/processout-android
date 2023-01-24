package com.processout.processout_sdk;

import java.util.ArrayList;

public interface FetchGatewaysConfigurationsCallback {
    void onSuccess(ArrayList<GatewayConfiguration> gateways);

    void onError(Exception e);
}
