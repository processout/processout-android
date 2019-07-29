package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class MiscGatewayRequest {
    @SerializedName("url")
    private String URL;

    @SerializedName("method")
    private String method;

    @SerializedName("headers")
    private Map<String, String> headers;

    @SerializedName("body")
    private String body;

    public MiscGatewayRequest(String body) {
        this.body = body;
    }

    public String getURL() {
        return URL;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
