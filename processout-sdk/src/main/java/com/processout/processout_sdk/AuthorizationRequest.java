package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class AuthorizationRequest {
    @SerializedName("source")
    private String source;
    @SerializedName("incremental")
    private boolean incremental;
    @SerializedName("enable_three_d_s_2")
    private boolean enableThreeDS2 = true;

    public AuthorizationRequest(String source, boolean incremental) {
        this.source = source;
        this.incremental = incremental;
    }

    public AuthorizationRequest(String source) {
        this.source = source;
        this.incremental = false;
    }

    public String getSource() {
        return source;
    }

    public boolean isEnableThreeDS2() {
        return enableThreeDS2;
    }
}
