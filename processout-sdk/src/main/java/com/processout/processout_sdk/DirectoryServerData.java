package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class DirectoryServerData {
    @SerializedName("directoryServerID")
    private String directoryServerID;

    @SerializedName("directoryServerPublicKey")
    private String directoryServerPublicKey;

    @SerializedName("threeDSServerTransID")
    private String threeDSServerTransactionID;

    public DirectoryServerData(String directoryServerID, String directoryServerPublicKey, String threeDSServerTransactionID) {
        this.directoryServerID = directoryServerID;
        this.directoryServerPublicKey = directoryServerPublicKey;
        this.threeDSServerTransactionID = threeDSServerTransactionID;
    }

    public String getDirectoryServerID() {
        return directoryServerID;
    }

    public String getDirectoryServerPublicKey() {
        return directoryServerPublicKey;
    }

    public String getThreeDSServerTransactionID() {
        return threeDSServerTransactionID;
    }
}
