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

    public AuthorizationRequest(String source, String invoiceID) {
        this.source = source;
        this.incremental = false;
        this.invoiceID = invoiceID;
    }


    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isIncremental() {
        return incremental;
    }
    public void setIncremental(boolean isIncremental) {
        this.incremental = isIncremental;
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
