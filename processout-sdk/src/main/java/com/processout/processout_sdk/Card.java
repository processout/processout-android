package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeremylejoux on 17/01/2018.
 */

public class Card {

    @SerializedName(("id"))
    private String id;
    @SerializedName("name")
    private String cardHolderName;
    @SerializedName("number")
    private String cardNumbers;
    @SerializedName("exp_month")
    private int expMonth;
    @SerializedName("exp_year")
    private int expYear;
    @SerializedName("cvc")
    private String cvc;
    @SerializedName("contact")
    private Contact contact;
    @SerializedName("token_type")
    private TokenType tokenType;
    @SerializedName("payment_token")
    private String paymentToken;

    public enum TokenType {
        googlepay
    }

    public Card(String id, String cvc) {
        this.id = id;
        this.cvc = cvc;
    }

    public Card(TokenType tokenType, String paymentToken, String cardHolderName, Contact contact) {
        this.tokenType = tokenType;
        this.paymentToken = paymentToken;
        this.cardHolderName = cardHolderName;
        this.contact = contact;
    }

    public Card(String cardNumbers, int expMonth, int expYear, String cvc) {
        this.cardNumbers = cardNumbers;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = cvc;
    }

    public Card(String cardHolderName, String cardNumbers, int expMonth, int expYear, String cvc) {
        this.cardHolderName = cardHolderName;
        this.cardNumbers = cardNumbers;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = cvc;
    }

    public Card(String cardHolderName, String cardNumbers, int expMonth, int expYear, String cvc, Contact contact) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.cardNumbers = cardNumbers;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = cvc;
        this.contact = contact;
    }

    public Card(String cardHolderName, String cardNumbers, int expMonth, int expYear, Contact contact) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.cardNumbers = cardNumbers;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardNumbers() {
        return cardNumbers;
    }

    public void setCardNumbers(String cardNumbers) {
        this.cardNumbers = cardNumbers;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
