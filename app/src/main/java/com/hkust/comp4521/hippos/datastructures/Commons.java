package com.hkust.comp4521.hippos.datastructures;

import android.util.Log;

import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TC on 4/29/2015.
 */
public class Commons {

    public static String appName = "hippos";

    private static User user = null;

    private static String[] INVENTORY_CATEGORY = {"Unsorted", "Books", "Confectionery", "Toys", "Stationery"};
    private static String[] SALESHISTORY_CATEGORY = {"Invoices", "Statistics", "Revenue"};

    // Adapter modes
    public static int MODE_NEW_INVOICE = 0;
    public static int MODE_SALES_CONFIRM = 1;

    private static List<Category> categoryList = null;
    private static HashMap<Integer, Inventory> inventoryHM = null;
    private static HashMap<Integer, ArrayList<Inventory>> categorizedinventoryHMList = null;
    private static int lastUpdate = -1;

    public static int getCategoryCount() {
        if(categoryList == null)
            return -1;
        return categoryList.size();
    }

    public static ArrayList<Inventory> getInventoryList(int index) {
        if(categorizedinventoryHMList == null)
            return null;
        return categorizedinventoryHMList.get(index);
    }

    public static Category getCategory(int cIndex) {
        if(categoryList == null)
            return null;
        return categoryList.get(cIndex);
    }

    public static Inventory getInventoryFromIndex(int cId, int iIndex) {
        if(categorizedinventoryHMList == null)
            return null;
        return categorizedinventoryHMList.get(cId).get(iIndex);
    }

    public static Inventory getInventory(int iIndex) {
        if(inventoryHM == null)
            return null;
        return inventoryHM.get(iIndex);
    }

    public static String[] getCategoryTabs() {
        if(categoryList == null)
            return null;
        return INVENTORY_CATEGORY;
    }

    public static Inventory getRandomInventory() {
        int catId = (int) (Math.random() * getCategoryCount());
        ArrayList<Inventory> list = categorizedinventoryHMList.get(catId);
        int invId = (int) (Math.random() * list.size());
        Inventory toReturn = list.get(invId);
        return toReturn;
    }

    public static String[] getSalesHistoryTabs() {
        return SALESHISTORY_CATEGORY;
    }

    public static void initializeInventoryList(final onInventoryListInitializedListener mListener) {
        // TODO: fetch list from server instead
        if(categorizedinventoryHMList != null) {
            if(mListener != null)
                mListener.onInitialized();
        }
        categorizedinventoryHMList = new HashMap<Integer, ArrayList<Inventory>>();
        inventoryHM = new HashMap<Integer, Inventory>();
        final RestClient rc = RestClient.getInstance();
        // Init Category information
        rc.getAllCategory(new RestListener<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (categories != null) {
                    Log.i("Commons", "Get category list");
                    categoryList = categories;
                    INVENTORY_CATEGORY = new String[categories.size()];
                    for (Category c : categories) {
                        categorizedinventoryHMList.put(c.getID(), new ArrayList<Inventory>());
                    }
                    for(int i = 0; i < categories.size(); i++) {
                        INVENTORY_CATEGORY[i] = categories.get(i).getName();
                    }
                    // Init Inventory information
                    rc.getAllInventory(new RestListener<List<Inventory>>() {
                        @Override
                        public void onSuccess(List<Inventory> netInventories) {
                            Log.i("Commons", "Get Inventory list");
                            for (Inventory inv : netInventories) {
                                ArrayList<Inventory> list = categorizedinventoryHMList.get(inv.getCategory());
                                list.add(inv);
                                inventoryHM.put(inv.getId(), inv);
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

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Commons.user = user;
    }
}
