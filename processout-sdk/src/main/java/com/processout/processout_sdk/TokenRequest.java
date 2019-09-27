package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class TokenRequest {
    @SerializedName("source")
    private String source;
    @SerializedName("verify")
    private boolean verify = true;
    @SerializedName("enable_three_d_s_2")
    private boolean enableThreeDS2 = true;

    TokenRequest(String source) {
        this.source = source;
    }
}
