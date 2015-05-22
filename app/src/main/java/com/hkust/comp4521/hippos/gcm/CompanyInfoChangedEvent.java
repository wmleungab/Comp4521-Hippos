package com.hkust.comp4521.hippos.gcm;

/**
 * Created by TC on 5/21/2015.
 */
public class CompanyInfoChangedEvent {

    public String name;
    public String email;
    public String phone;
    public String address;

    public CompanyInfoChangedEvent(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
