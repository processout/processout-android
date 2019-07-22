package com.processout.processout_sdk;

public class WebViewReturnAction {

    public enum WebViewReturnType {
        APMAuthorization,
        ThreeDSVerification,
    }

    private boolean success;
    private WebViewReturnType type;
    private String value;

    public WebViewReturnAction(boolean success, WebViewReturnType type, String value) {
        this.success = success;
        this.type = type;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public WebViewReturnType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
