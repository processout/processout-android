package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class Device {
    @SerializedName("channel")
    private String channel;

    public Device(String channel) {
        this.channel = channel;
    }
}

class Invoice {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private String amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("device")
    private Device device;

    public Invoice(String name, String amount, String currency, Device device) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Device getDevice() {
        return device;
    }
}
