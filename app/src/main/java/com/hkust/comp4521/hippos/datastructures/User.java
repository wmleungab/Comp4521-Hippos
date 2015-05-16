package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 15/5/2015.
 */
public class User {
    public int id;
    public String name;
    public String email;
    public String password;
    public String apiKey;
    public String createdAt;

    public User(String name, String email, String password, String apiKey, String createdAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.apiKey = apiKey;
        this.createdAt = createdAt;
    }
}
