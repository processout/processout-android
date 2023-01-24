package com.processout.processout_sdk;

public class SDKEPhemPubKey {
    private String crv;
    private String kty;
    private String x;
    private String y;

    public SDKEPhemPubKey(String crv, String kty, String x, String y) {
        this.crv = crv;
        this.kty = kty;
        this.x = x;
        this.y = y;
    }
}
