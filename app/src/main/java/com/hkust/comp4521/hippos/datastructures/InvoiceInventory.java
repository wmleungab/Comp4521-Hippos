package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by TC on 4/2/2015.
 */
public class InvoiceInventory {

    private Inventory inventory;
    private int quantity;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public InvoiceInventory(Inventory mInv, int mQuantity) {
        inventory = mInv;
        quantity = mQuantity;
    }

}
