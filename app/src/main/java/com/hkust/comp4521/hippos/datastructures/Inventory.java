package com.hkust.comp4521.hippos.datastructures;

import android.graphics.drawable.Drawable;

/**
 * Created by Yman on 15/5/2015.
 */
public class Inventory {

    // Flags
    public static String INVENTORY_INV_ID = "inventory_invId";
    public static int INVENTORY_DISABLED = 0;
    public static int INVENTORY_NORMAL = 1;
    public static int INVENTORY_DIRTY = 2;

    // Attributes
    int id;
    String name;
    double price;
    int stock;
    String image; //image location on the server of this inventory
    int status;     // 0 - disabled; 1 - normal; 2 - dirty
    String timestamp;
    int category;

    public Inventory() {

    }

    public Inventory(int id, String name, double price, int stock, String image, int status, String timestamp, int category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.status = status;
        this.timestamp = timestamp;
        this.category = category;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFormattedPrice() {
        return "$" + String.format("%.1f", price);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public class DrawableContainer {
        public Drawable drawable = null;
    }
}
