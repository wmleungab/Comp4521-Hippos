package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 15/5/2015.
 */
public class Inventory {

    // Attributes
    int id;
    String name;
    double price;
    int stock;
    int status;
    String timestamp;
    int category;

    // Flags
    public static String INVENTORY_CAT_ID = "inventory_catId";
    public static String INVENTORY_INV_ID = "inventory_invId";

    public Inventory() {

    }

    public Inventory(int id, String name, double price, int stock, int status, String timestamp, int category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.timestamp = timestamp;
        this.category = category;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return "$" + String.format("%.1f", price);
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
