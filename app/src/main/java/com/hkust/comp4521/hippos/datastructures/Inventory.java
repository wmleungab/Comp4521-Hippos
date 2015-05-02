package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by TC on 4/2/2015.
 */
public class Inventory {
    private long _id;
    private String name;
    private int catId;
    private float price;
    private String fileName;

    public static String INVENTORY_CAT_ID = "inventory_catId";
    public static String INVENTORY_INV_ID = "inventory_invId";

    public Inventory() {
        _id = -2;
    }

    public Inventory(String pName, int pCatId, int pPrice, String pfileName) {
        _id = -1;
        name = pName;
        catId = pCatId;
        price = pPrice;
        fileName = pfileName;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getFileName() { return fileName; }
}
