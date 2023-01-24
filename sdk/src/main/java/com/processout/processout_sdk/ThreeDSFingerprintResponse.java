package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class ThreeDSFingerprintResponse {
    @SerializedName("sdkEncData")
    private String sdkEncData;
    @SerializedName("deviceChannel")
    private String deviceChannel = "app";
    @SerializedName("sdkAppID")
    private String sdkAppID;
    @SerializedName("sdkEphemPubKey")
    private SDKEPhemPubKey sdkEphemPubKey;
    @SerializedName("sdkReferenceNumber")
    private String sdkReferenceNumber;
    @SerializedName("sdkTransID")
    private String sdkTransID;

    public ThreeDSFingerprintResponse(String sdkEncData, String sdkAppID, SDKEPhemPubKey sdkEphemPubKey, String sdkReferenceNumber, String sdkTransID) {
        this.sdkEncData = sdkEncData;
        this.sdkAppID = sdkAppID;
        this.sdkEphemPubKey = sdkEphemPubKey;
        this.sdkReferenceNumber = sdkReferenceNumber;
        this.sdkTransID = sdkTransID;
    }

    public String getSdkEncData() {
        return sdkEncData;
    }

    public String getDeviceChannel() {
        return deviceChannel;
    }

    public String getSdkAppID() {
        return sdkAppID;
    }

    public SDKEPhemPubKey getSdkEphemPubKey() {
        return sdkEphemPubKey;
    }

    public String getSdkReferenceNumber() {
        return sdkReferenceNumber;
    }

    public String getSdkTransID() {
        return sdkTransID;
    }
}
