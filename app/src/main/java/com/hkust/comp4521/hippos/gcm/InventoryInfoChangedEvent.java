package com.hkust.comp4521.hippos.gcm;

import com.hkust.comp4521.hippos.datastructures.Inventory;

/**
 * Created by TC on 5/21/2015.
 */
public class InventoryInfoChangedEvent {

    public boolean refreshAll = false;
    private Inventory inv;

    public InventoryInfoChangedEvent(Inventory inv) {
        this.inv = inv;
    }

    public InventoryInfoChangedEvent() {
        refreshAll = true;
    }

    public Inventory getInventory() {
        return inv;
    }
}
