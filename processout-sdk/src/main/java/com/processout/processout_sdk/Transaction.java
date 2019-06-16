package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class Transaction {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("currency")
    private String currency;

    @SerializedName("sandbox")
    private boolean sandbox;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("invoice_id")
    private String invoiceId;

    @SerializedName("status")
    private String status;

    @SerializedName("amount")
    private String amount;

    public Transaction(String id, String currency, String amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }
}
