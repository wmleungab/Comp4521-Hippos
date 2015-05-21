package com.hkust.comp4521.hippos.gcm;

import com.hkust.comp4521.hippos.datastructures.Inventory;

/**
 * Created by TC on 5/21/2015.
 */
public class InventoryInfoChangedEvent {

    private Inventory inv;

    public InventoryInfoChangedEvent(Inventory inv) {
        this.inv = inv;
    }

    public Inventory getInventory() {
        return inv;
    }
}
