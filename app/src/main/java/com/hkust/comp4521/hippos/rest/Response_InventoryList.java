package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.NetInventory;

import java.util.List;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_InventoryList {
    public boolean error;
    public String message;
    List<NetInventory> inventory;

    public List<NetInventory> getInventoryList() {
        return inventory;
    }
}
