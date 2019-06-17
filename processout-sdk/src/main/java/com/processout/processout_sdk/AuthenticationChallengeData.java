package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class AuthenticationChallengeData {
    @SerializedName("acsTransID")
    private String acsTransID;
    @SerializedName("acsReferenceNumber")
    private String acsReferenceNumber;
    @SerializedName("acsSignedContent")
    private String acsSignedContent;
    @SerializedName("threeDSServerTransID")
    private String threeDSServerTransID;


    public AuthenticationChallengeData(String acsTransID, String acsReferenceNumber, String acsSignedContent, String threeDSServerTransID) {
        this.acsTransID = acsTransID;
        this.acsReferenceNumber = acsReferenceNumber;
        this.acsSignedContent = acsSignedContent;
        this.threeDSServerTransID = threeDSServerTransID;
    }


    public String getAcsTransID() {
        return acsTransID;
    }

    public String getAcsReferenceNumber() {
        return acsReferenceNumber;
    }

    public String getAcsSignedContent() {
        return acsSignedContent;
    }

    public String getThreeDSServerTransID() {
        return threeDSServerTransID;
    }
}
