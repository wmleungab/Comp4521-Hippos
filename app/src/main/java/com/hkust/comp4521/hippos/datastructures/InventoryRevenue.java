package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 22/5/2015.
 */
public class InventoryRevenue {

    public int id;
    public String name;
    public double price;
    public int quantity;
    public double revenue;

    public String getFormattedRevenue() {
        return "$" + String.format("%.1f", revenue);
    }

    public int getQuantity() {
        return quantity;
    }
}
