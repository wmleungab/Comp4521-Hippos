package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 15/5/2015.
 */
public class Invoice {
    int id;
    double total_price;
    double final_price;
    String date_time;
    int user;
    String content;
    String email;
    int status;

    public int getID() {
        return id;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public double getFinalPrice() {
        return final_price;
    }

    public String getDateTime() {
        return date_time;
    }

    public int getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }
}
