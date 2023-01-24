package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class DirectoryServerData {
    @SerializedName("directoryServerID")
    private String directoryServerID;

    @SerializedName("directoryServerPublicKey")
    private String directoryServerPublicKey;

    @SerializedName("threeDSServerTransID")
    private String threeDSServerTransactionID;

    @SerializedName("messageVersion")
    private String messageVersion;

    public DirectoryServerData(String directoryServerID, String directoryServerPublicKey,
                               String threeDSServerTransactionID, String messageVersion ) {
        this.directoryServerID = directoryServerID;
        this.directoryServerPublicKey = directoryServerPublicKey;
        this.threeDSServerTransactionID = threeDSServerTransactionID;
        this.messageVersion = messageVersion;
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

    public String getMessageVersion() {
        return messageVersion;
    }
}
