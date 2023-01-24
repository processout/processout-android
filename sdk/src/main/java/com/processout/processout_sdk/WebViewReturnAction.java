package com.processout.processout_sdk;

public class WebViewReturnAction {

    public enum WebViewReturnType {
        APMAuthorization,
        ThreeDSVerification,
        ThreeDSFallbackVerification,
    }

    private boolean success;
    private WebViewReturnType type;
    private String value;
    private String invoiceId;

    public WebViewReturnAction(boolean success, WebViewReturnType type, String value) {
        this.success = success;
        this.type = type;
        this.value = value;
    }

    public WebViewReturnAction(boolean success, WebViewReturnType type, String value, String invoiceId) {
        this.success = success;
        this.type = type;
        this.value = value;
        this.invoiceId = invoiceId;
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

    public String getInvoiceId() {
        return invoiceId;
    }
}
