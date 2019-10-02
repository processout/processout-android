package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class CustomerAction {

    protected enum CustomerActionType {
        @SerializedName("fingerprint-mobile")
        FINGERPRINT_MOBILE,
        @SerializedName("challenge-mobile")
        CHALLENGE_MOBILE,
        @SerializedName("url")
        URL,
        @SerializedName("redirect")
        REDIRECT,
        @SerializedName("fingerprint")
        FINGERPRINT
    }
    
    @SerializedName("type")
    private CustomerActionType type;
    @SerializedName("value")
    private String value;

    public CustomerAction(CustomerActionType type, String value) {
        this.type = type;
        this.value = value;
    }

    public CustomerActionType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
