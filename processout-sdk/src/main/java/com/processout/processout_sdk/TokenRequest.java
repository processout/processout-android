package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("customer_id")
    private String customerID;
    @SerializedName("token_id")
    private String tokenID;
    @SerializedName("source")
    private String source;
    @SerializedName("verify")
    private final boolean verify = true;
    @SerializedName("enable_three_d_s_2")
    private final boolean enableThreeDS2 = true;
    @SerializedName("third_party_sdk_version")
    private String thirdPartySDKVersion;

    @SerializedName("preferred_scheme")
    private String preferredScheme;

    TokenRequest(String source) {
        this.source = source;
    }

     TokenRequest(String source, String thirdPartySDKVersion) {
        this.source = source;
        this.thirdPartySDKVersion = thirdPartySDKVersion;
    }

    TokenRequest(String tokenID, String customerID, String source) {
        this.tokenID = tokenID;
        this.customerID = customerID;
        this.source = source;
    }

    public String getCustomerID() {
        return customerID;
    }
    public String getTokenID() {
        return tokenID;
    }
    public String getSource() {
        return source;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThirdPartySDKVersion() {
        return thirdPartySDKVersion;
    }

    public void setThirdPartySDKVersion(String thirdPartySDKVersion) {
        this.thirdPartySDKVersion = thirdPartySDKVersion;
    }

    public String getPreferredScheme() {
        return preferredScheme;
    }

    public void setPreferredScheme(String preferredScheme) {
        this.preferredScheme = preferredScheme;
    }
}
