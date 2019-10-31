package com.processout.processout_sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class GatewayConfiguration {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("enabled")
    private boolean enabled;
    @SerializedName("default_currency")
    private String defaultCurrency;
    @SerializedName("gateway")
    private Gateway gateway;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public Gateway getGateway() {
        return gateway;
    }
}
