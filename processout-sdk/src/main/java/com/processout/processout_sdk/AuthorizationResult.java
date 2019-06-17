package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;
import com.processout.processout_sdk.CustomerAction;

class AuthorizationResult {
    @SerializedName("customer_action")
    private CustomerAction customerAction;

    @SerializedName("transaction")
    private Transaction transaction;

    public AuthorizationResult(CustomerAction customerAction, Transaction transaction) {
        this.customerAction = customerAction;
        this.transaction = transaction;
    }

    public CustomerAction getCustomerAction() {
        return customerAction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
