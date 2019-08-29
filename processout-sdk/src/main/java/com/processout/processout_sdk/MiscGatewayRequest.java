package com.processout.processout_sdk;

import android.util.Base64;

import com.google.gson.Gson;
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

    public String generateToken() {
        return Base64.encodeToString(new Gson().toJson(this, MiscGatewayRequest.class).getBytes(), Base64.NO_WRAP);
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }
}
