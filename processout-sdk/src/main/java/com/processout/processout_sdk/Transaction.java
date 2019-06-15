package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

class Transaction {
    @SerializedName("id")
    private String id;

    @SerializedName("project_id")
    private String projectId;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("subscription_id")
    private String subscriptionId;

    @SerializedName("name")
    private String name;

    @SerializedName("currency")
    private String currency;

    @SerializedName("sandbox")
    private boolean sandbox;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("gateway_name")
    private String gatewayName;

    @SerializedName("invoice_id")
    private String invoiceId;

    @SerializedName("status")
    private String status;

    @SerializedName("duplicate_distance_seconds")
    private boolean duplicateDistanceSeconds;

    @SerializedName("three_d_s_status")
    private String threeDSStatus;

    @SerializedName("three_d_s_version")
    private String threeDSVersion;

    @SerializedName("gateway_configuration_id")
    private String gatewayConfigurationId;

    @SerializedName("token_id")
    private String tokenId;

    @SerializedName("card_id")
    private String cardId;

    @SerializedName("error_code")
    private String errorCode;

    @SerializedName("payment_type")
    private String paymentType;

    @SerializedName("eci")
    private String ECI;

    @SerializedName("mcc")
    private String MCC;

    @SerializedName("merchant_account_id")
    private String merchantAccountId;

    @SerializedName("is_on_us")
    private boolean isOnUs;

    @SerializedName("cvc_check")
    private String CVCCheck;

    @SerializedName("avs_check")
    private String AVSCheck;

    @SerializedName("amount")
    private String amount;

    @SerializedName("amount_local")
    private String amountLocal;

    @SerializedName("authorized_amount")
    private String authorizedAmount;

    @SerializedName("authorized_amount_local")
    private String authorizedAmountLocal;

    @SerializedName("captured_amount")
    private String capturedAmount;

    @SerializedName("captured_amount_local")
    private String capturedAmountLocal;

    @SerializedName("available_amount")
    private String availableAmount;

    @SerializedName("available_amount_local")
    private String availableAmountLocal;

    @SerializedName("refunded_mount")
    private String refundedAmount;

    @SerializedName("refunded_amount_local")
    private String refundedAmountLocal;

    @SerializedName("currency_fee")
    private String currencyFee;

    @SerializedName("gateway_fee")
    private String gatewayFee;

    @SerializedName("gateway_fee_local")
    private String gatewayFeeLocal;

    @SerializedName("chargedback_at")
    private String chargedbackAt;

    @SerializedName("authorized")
    private boolean authorized;

    @SerializedName("captured")
    private boolean captured;

    @SerializedName("refunded")
    private boolean refunded;

    @SerializedName("voided")
    private boolean voided;

    @SerializedName("chargedback")
    private boolean chargedback;

    public Transaction(String id, String currency, String amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
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

    public String getGatewayName() {
        return gatewayName;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDuplicateDistanceSeconds() {
        return duplicateDistanceSeconds;
    }

    public String getThreeDSStatus() {
        return threeDSStatus;
    }

    public String getThreeDSVersion() {
        return threeDSVersion;
    }

    public String getGatewayConfigurationId() {
        return gatewayConfigurationId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getCardId() {
        return cardId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getECI() {
        return ECI;
    }

    public String getMCC() {
        return MCC;
    }

    public String getMerchantAccountId() {
        return merchantAccountId;
    }

    public boolean isOnUs() {
        return isOnUs;
    }

    public String getCVCCheck() {
        return CVCCheck;
    }

    public String getAVSCheck() {
        return AVSCheck;
    }

    public String getAmount() {
        return amount;
    }

    public String getAmountLocal() {
        return amountLocal;
    }

    public String getAuthorizedAmount() {
        return authorizedAmount;
    }

    public String getAuthorizedAmountLocal() {
        return authorizedAmountLocal;
    }

    public String getCapturedAmount() {
        return capturedAmount;
    }

    public String getCapturedAmountLocal() {
        return capturedAmountLocal;
    }

    public String getAvailableAmount() {
        return availableAmount;
    }

    public String getAvailableAmountLocal() {
        return availableAmountLocal;
    }

    public String getRefundedAmount() {
        return refundedAmount;
    }

    public String getRefundedAmountLocal() {
        return refundedAmountLocal;
    }

    public String getCurrencyFee() {
        return currencyFee;
    }

    public String getGatewayFee() {
        return gatewayFee;
    }

    public String getGatewayFeeLocal() {
        return gatewayFeeLocal;
    }

    public String getChargedbackAt() {
        return chargedbackAt;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public boolean isCaptured() {
        return captured;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public boolean isVoided() {
        return voided;
    }

    public boolean isChargedback() {
        return chargedback;
    }
}
