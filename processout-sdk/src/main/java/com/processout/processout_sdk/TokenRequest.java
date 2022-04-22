package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class TokenRequest {
    @SerializedName("source")
    private String source;
    @SerializedName("verify")
    private boolean verify = true;
    @SerializedName("enable_three_d_s_2")
    private boolean enableThreeDS2 = true;
    @SerializedName("third_party_sdk_version")
    private String thirdPartySDKVersion;

    TokenRequest(String source) {
        this.source = source;
    }

     TokenRequest(String source, String thirdPartySDKVersion) {
        this.source = source;
        this.thirdPartySDKVersion = thirdPartySDKVersion;
    }
}
