package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class AuthorizationRequest {
    @SerializedName("source")
    private String source;
    @SerializedName("incremental")
    private boolean incremental;
    @SerializedName("enable_three_d_s_2")
    private boolean enableThreeDS2 = true;
    @SerializedName("third_party_sdk_version")
    private String thirdPartySDKVersion;
    @SerializedName("preferred_scheme")
    private String preferredScheme;

    public AuthorizationRequest(String source, boolean incremental) {
        this.source = source;
        this.incremental = incremental;
    }

    public AuthorizationRequest(String source, boolean incremental, String thirdPartySDKVersion) {
        this.source = source;
        this.incremental = incremental;
        this.thirdPartySDKVersion = thirdPartySDKVersion;
    }

    public AuthorizationRequest(String source) {
        this.source = source;
        this.incremental = false;
    }

    public AuthorizationRequest(String source, String thirdPartySDKVersion) {
        this.source = source;
        this.incremental = false;
        this.thirdPartySDKVersion = thirdPartySDKVersion;
    }

    public AuthorizationRequest(String source, boolean incremental, String thirdPartySDKVersion, String preferredScheme) {
        this.source = source;
        this.incremental = incremental;
        this.thirdPartySDKVersion = thirdPartySDKVersion;
        this.preferredScheme = preferredScheme;
    }

    public String getSource() {
        return source;
    }

    public boolean isEnableThreeDS2() {
        return enableThreeDS2;
    }

    public String getThirdPartySDKVersion() {
        return thirdPartySDKVersion;
    }
}
