package com.processout.processout_sdk;

import androidx.annotation.NonNull;

import com.processout.processout_sdk.ProcessOutExceptions.ProcessOutException;

public class APMTokenReturn {

    public enum APMReturnType {
        Authorization,
        TokenCreation
    }

    private String token;
    private String tokenId;
    private String customerId;
    private APMReturnType type;
    private ProcessOutException error;

    public APMTokenReturn(@NonNull String token, @NonNull String customerId, @NonNull String tokenId) {
        this.token = token;
        this.tokenId = tokenId;
        this.customerId = customerId;
        this.type = APMReturnType.TokenCreation;
    }

    public APMTokenReturn(@NonNull String token) {
        this.token = token;
        this.type = APMReturnType.Authorization;
    }

    public APMTokenReturn(ProcessOutException error) {
        this.error = error;
    }

    public ProcessOutException getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public APMReturnType getType() {
        return type;
    }
}
