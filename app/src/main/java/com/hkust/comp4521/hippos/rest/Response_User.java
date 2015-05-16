package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.User;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_User extends User {
    public boolean error;
    public String message;

    public Response_User(String name, String email, String password, String apiKey, String createdAt) {
        super(name, email, password, apiKey, createdAt);
    }

    public User getUser() {
        return (User) this;
    }
}
