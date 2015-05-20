package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by TC on 4/2/2015.
 */
public class InvoiceInventory {

    // Attributes
    private Inventory inventory;
    private int quantity;
    private double price;

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

    public double getPrice() {
        price = inventory.getPrice();
        price *= quantity;
        return price;
    }

    public String getFormattedPrice() {
        price = inventory.getPrice();
        price *= quantity;
        return "$" + String.format("%.1f", price);
    }

    public InvoiceInventory(Inventory mInv, int mQuantity) {
        inventory = mInv;
        quantity = mQuantity;
    }

}
