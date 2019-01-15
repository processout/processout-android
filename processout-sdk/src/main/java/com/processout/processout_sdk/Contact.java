package com.processout.processout_sdk;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("address1")
    private String Address1;
    @SerializedName("address2")
    private String Address2;
    @SerializedName("city")
    private String City;
    @SerializedName("state")
    private String State;
    @SerializedName("country_code")
    private String CountryCode;
    @SerializedName("zip")
    private String Zip;

    public Contact(String address1, String address2, String city, String state, String countryCode, String zip) {
        Address1 = address1;
        Address2 = address2;
        City = city;
        State = state;
        CountryCode = countryCode;
        Zip = zip;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }
}