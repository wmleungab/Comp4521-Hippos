package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 15/5/2015.
 */
public class NetInventory {
    int id;
    String name;
    double price;
    int stock;
    int status;
    String timestamp;
    int category;

    public NetInventory(int id, String name, double price, int stock, int status, String timestamp, int category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.timestamp = timestamp;
        this.category = category;

    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getStatus() {
        return status;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public int getCategory() {
        return category;
    }
}
