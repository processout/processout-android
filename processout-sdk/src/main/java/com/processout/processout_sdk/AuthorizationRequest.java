package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class AuthorizationRequest {
    @SerializedName("invoice_id")
    private String invoiceID;
    @SerializedName("source")
    private String source;
    @SerializedName("incremental")
    private boolean incremental;
    @SerializedName("enable_three_d_s_2")
    private final boolean enableThreeDS2 = true;
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

    public AuthorizationRequest(String invoiceID, String source, String thirdPartySDKVersion, String preferredScheme) {
        this.source = source;
        this.invoiceID = invoiceID;
        this.incremental = false;
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
    public String getInvoiceID() {
        return invoiceID;
    }

    public String getPreferredScheme() {
        return preferredScheme;
    }

    public void setIncremental(boolean isIncremental) {
        this.incremental = isIncremental;
    }
}
