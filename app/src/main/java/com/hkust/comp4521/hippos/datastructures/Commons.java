package com.hkust.comp4521.hippos.datastructures;

import android.util.Log;
import android.widget.TextView;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TC on 4/29/2015.
 */
public class Commons {
    private static String[] INVENTORY_CATEGORY = {"Unsorted", "Books", "Confectionery", "Toys", "Stationery"};
    private static String[] SALESHISTORY_CATEGORY = {"Invoices", "Statistics", "Revenue"};

    // Adapter modes
    public static int MODE_NEW_INVOICE = 0;
    public static int MODE_SALES_CONFIRM = 1;

    private static HashMap<Integer, ArrayList<Inventory>> inventoryList = null;
    private static int lastUpdate = -1;

    public static int getCategoryCount() {
        return inventoryList.size();
    }

    public static ArrayList<Inventory> getInventoryList(int index) {
        return inventoryList.get(index);
    }

    public static Inventory getInventory(int cIndex, int iIndex) {
        return inventoryList.get(cIndex).get(iIndex);
    }

    public static String[] getCategoryTabs() {
        return INVENTORY_CATEGORY;
    }

    public static Inventory getRandomInventory() {
        int catId = (int) (Math.random() * getCategoryCount());
        ArrayList<Inventory> list = inventoryList.get(catId);
        int invId = (int) (Math.random() * list.size());
        Inventory toReturn = list.get(invId);
        return toReturn;
    }

    public static String[] getSalesHistoryTabs() {
        return SALESHISTORY_CATEGORY;
    }

    public static void initializeInventoryList(final onInventoryListInitializedListener mListener) {
        // TODO: fetch list from server instead
        if(inventoryList != null) {
            if(mListener != null)
                mListener.onInitialized();
        }
        inventoryList = new HashMap<Integer, ArrayList<Inventory>>();
        final RestClient rc = RestClient.getInstance();
        rc.getAllCategory(new RestListener<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories != null) {
                    Log.i("Commons", "Get category list");
                    for (Category c : categories) {
                        inventoryList.put(c.getID(), new ArrayList<Inventory>());
                    }
                    rc.getAllInventory(new RestListener<List<Inventory>>() {
                        @Override
                        public void onSuccess(List<Inventory> netInventories) {
                            Log.i("Commons", "Get Inventory list");
                            for (Inventory inv : netInventories) {
                                //inventoryList.put(c.getID(), new ArrayList<Inventory>());
                                ArrayList<Inventory> list = inventoryList.get(inv.getCategory());
                                list.add(inv);
                            }
                            if(mListener != null)
                                mListener.onInitialized();
                        }
                        @Override
                        public void onFailure(int status) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }

    public interface onInventoryListInitializedListener {
        public void onInitialized();
    }
}
