package com.hkust.comp4521.hippos.datastructures;

import android.os.Environment;
import android.util.Log;

import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TC on 4/29/2015.
 */
public class Commons {

    // Trivial information
    public static String appName = "hippos";
    public static String APP_ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + Commons.appName + File.separator;

    // Server information
    private static User user = null;

    // ViewPager Tabs
    private static String[] INVENTORY_CATEGORY = {};
    private static String[] SALESHISTORY_CATEGORY = {"Invoices", "Statistics", "Revenue"};

    // Adapter modes
    public static int MODE_NEW_INVOICE = 0;
    public static int MODE_SALES_CONFIRM = 1;

    // Data structure for inventory
    private static List<Category> categoryList = null;
    private static HashMap<Integer, Inventory> inventoryHM = null;
    private static HashMap<Integer, ArrayList<Inventory>> categorizedinventoryHMList = null;
    private static int lastUpdate = -1;

    // Data structure for invoices
    private static List<Invoice> invoiceList = null;

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

    public static int getCategoryIndex(int cId) {
        if(categoryList == null)
            return -1;
        int toReturn = -1;
        for(int i=0; i<categoryList.size(); i++) {
            if(categoryList.get(i).getID() == cId)
                toReturn = i;
        }
        return toReturn;
    }

    public static List<Category> getCategoryList() {
        if(categoryList == null)
            return null;
        return categoryList;
    }

    public static Inventory getInventoryFromIndex(int cId, int iIndex) {
        if(categorizedinventoryHMList == null)
            return null;
        return categorizedinventoryHMList.get(cId).get(iIndex);
    }

    public static Inventory getInventory(int iId) {
        if(inventoryHM == null)
            return null;
        return inventoryHM.get(iId);
    }

    public static String[] getCategoryTabs() {
        if(categoryList == null)
            return null;
        return INVENTORY_CATEGORY;
    }

    public static Inventory getRandomInventory() {
        int catId = (int) (Math.random() * getCategoryCount());
        ArrayList<Inventory> list = categorizedinventoryHMList.get(categoryList.get(catId).getID());
        int invId = (int) (Math.random() * list.size());
        Inventory toReturn = list.get(invId);
        return toReturn;
    }

    public static String[] getSalesHistoryTabs() {
        return SALESHISTORY_CATEGORY;
    }

    public static void initializeInventoryList(final onInitializedListener mListener) {
        // TODO: fetch list from local DB first, go to remote server if local DB does not exist
        if(categorizedinventoryHMList != null) {
            if(mListener != null) {
                mListener.onInitialized();
                return;
            }
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


    public static void initializeInvoiceList(final onInitializedListener mListener) {
        RestClient.getInstance().getAllInvoice(new RestListener<List<Invoice>>() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                invoiceList = invoices;
                mListener.onInitialized();
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }

    public static List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public static Invoice getInvoice(int invoiceIdx) {
        if(invoiceList == null)
            return null;
        return invoiceList.get(invoiceIdx);
    }

    public interface onInitializedListener {
        public void onInitialized();
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Commons.user = user;
    }
}
