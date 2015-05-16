package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Inventory;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_Inventory extends Inventory {
    public boolean error;
    public String message;


    public Response_Inventory(int id, String name, double price, int stock, int status, String timestamp, int category) {
        super(id, name, price, stock, status, timestamp, category);
    }

    public Inventory getInventory() {
        return (Inventory) this;
    }
}



