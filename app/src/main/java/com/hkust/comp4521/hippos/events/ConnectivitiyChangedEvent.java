package com.hkust.comp4521.hippos.events;

import com.hkust.comp4521.hippos.datastructures.Inventory;

/**
 * Created by TC on 5/21/2015.
 */
public class ConnectivitiyChangedEvent {

    public boolean onlineMode = false;

    public ConnectivitiyChangedEvent(boolean isOnline) {
        onlineMode = isOnline;
    }

}
