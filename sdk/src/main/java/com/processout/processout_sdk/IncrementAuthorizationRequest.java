package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class IncrementAuthorizationRequest {
    @SerializedName("amount")
    private int amount;

    public IncrementAuthorizationRequest(int amount) {
        this.amount = amount;
    }
}
