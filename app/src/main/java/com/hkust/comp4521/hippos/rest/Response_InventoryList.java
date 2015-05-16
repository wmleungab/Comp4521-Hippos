package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Inventory;

import java.util.List;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_InventoryList {
    public boolean error;
    public String message;
    List<Inventory> inventory;

    public List<Inventory> getCategoryList() {

        return this.inventory;
    }

    public List<Inventory> getInventoryList() {
        return inventory;
    }
}
