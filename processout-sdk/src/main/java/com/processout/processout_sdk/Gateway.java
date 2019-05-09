package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Gateway {
    @SerializedName("name")
    private String name;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("logo_url")
    private String logoUrl;
    @SerializedName("tags")
    private ArrayList<String> tags;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
