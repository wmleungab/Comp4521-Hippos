package com.hkust.comp4521.hippos.datastructures;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.database.CategoryDB;
import com.hkust.comp4521.hippos.database.DatabaseHelper;
import com.hkust.comp4521.hippos.database.InventoryDB;
import com.hkust.comp4521.hippos.database.InvoiceDB;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.squareup.otto.Bus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TC on 4/29/2015.
 */
public class Commons {

    // Application information
    public static String appName = "hippos";
    public static String APP_ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + Commons.appName + File.separator;
    public static String GCM_PROJECT_NUMBER = "19676954580";
    public static boolean ONLINE_MODE = true;
    private static User user = null;

    // Bus event
    private static Bus mBus = new Bus();

    // ViewPager Tabs
    private static String[] INVENTORY_CATEGORY = {};
    private static String[] SALESHISTORY_CATEGORY = null;

    // Adapter modes
    public static int MODE_NEW_INVOICE = 0;
    public static int MODE_SALES_CONFIRM = 1;

    // Data structure
    private static List<Category> categoryList = null;
    private static HashMap<Integer, Inventory> inventoryHM = null;
    private static HashMap<Integer, ArrayList<Inventory>> categorizedinventoryHMList = null;
    private static List<Invoice> localInvoiceList = null;
    private static List<Invoice> remoteInvoiceList = null;

    // Getter for Bus event
    public static Bus getBusInstance() {
        return mBus;
    }

    // This method initialize inventory and category list
    public static void forceUpdateInventoryList(final onInitializedListener mListener) {
        categorizedinventoryHMList = new HashMap<Integer, ArrayList<Inventory>>();
        inventoryHM = new HashMap<Integer, Inventory>();
        final InventoryDB inventoryHelper = InventoryDB.getInstance();
        final CategoryDB categoryHelper = CategoryDB.getInstance();
        DatabaseHelper.reinitDatabase();
        // Inventory table not yet initialized, get data from server
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
                        categoryHelper.insert(c);
                    }
                    for (int i = 0; i < categories.size(); i++) {
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
                                inventoryHelper.insert(inv);
                            }
                            if (mListener != null)
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

    public static void recoverLoginInformation(Context mContext, final onInitializedListener mListener) {
        DatabaseHelper.initDatabase(mContext);
        PreferenceService.initPreference(mContext);
        // Recover user information if needed
        if(user == null) {
            String name = PreferenceService.getStringValue(PreferenceService.KEY_LOGIN_NAME);
            String password = PreferenceService.getStringValue(PreferenceService.KEY_LOGIN_PASSWORD);
            String email = PreferenceService.getStringValue(PreferenceService.KEY_LOGIN_USERNAME);
            String apiKey = PreferenceService.getStringValue(PreferenceService.KEY_LOGIN_API_KEY);
            user = new User(name, email, password, apiKey, "");
        }
        if(mListener != null)
            mListener.onInitialized();
    }

    public static void initializeInventoryList(final onInitializedListener mListener) {
        if(categorizedinventoryHMList == null || inventoryHM == null) {
            // fetch list from local DB first, go to remote server if local DB does not exist
            categorizedinventoryHMList = new HashMap<Integer, ArrayList<Inventory>>();
            inventoryHM = new HashMap<Integer, Inventory>();
            final InventoryDB inventoryHelper = InventoryDB.getInstance();
            final CategoryDB categoryHelper = CategoryDB.getInstance();
            if(inventoryHelper.getCount() > 0) {
                // Inventory table is initialized, but not loaded
                // Init Category information
                List<Category> categories = categoryHelper.getAll();
                if (categories != null) {
                    Log.i("Commons", "Get category list");
                    categoryList = categories;
                    INVENTORY_CATEGORY = new String[categories.size()];
                    for (Category c : categories) {
                        categorizedinventoryHMList.put(c.getID(), new ArrayList<Inventory>());
                    }
                    for (int i = 0; i < categories.size(); i++) {
                        INVENTORY_CATEGORY[i] = categories.get(i).getName();
                    }
                    List<Inventory> netInventories = inventoryHelper.getAll();
                    Log.i("Commons", "Inventory table already initialized, load from local DB instead");
                    for (Inventory inv : netInventories) {
                        ArrayList<Inventory> list = categorizedinventoryHMList.get(inv.getCategory());
                        list.add(inv);
                        inventoryHM.put(inv.getId(), inv);
                    }
                    if(mListener != null)
                        mListener.onInitialized();
                }
            } else {
                forceUpdateInventoryList(mListener);
            }
        } else {
            // Inventory table is initialized already
            if(mListener != null)
                mListener.onInitialized();
        }
    }

    // For category-related functions
    public static int getCategoryCount() {
        if(categoryList == null)
            return -1;
        return categoryList.size();
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

    // For inventory-related functions
    public static Inventory getInventory(int iId) {
        if(inventoryHM == null)
            return null;
        return inventoryHM.get(iId);
    }
    public static ArrayList<Inventory> getInventoryList(int index) {
        if(categorizedinventoryHMList == null)
            return null;
        return categorizedinventoryHMList.get(index);
    }
    public static Inventory getInventoryFromIndex(int cId, int iIndex) {
        if(categorizedinventoryHMList == null)
            return null;
        return categorizedinventoryHMList.get(cId).get(iIndex);
    }

    // For invoice-related functions
    public static void initializeInvoiceList(final onInitializedListener mListener) {
        RestClient.getInstance().getAllInvoice(new RestListener<List<Invoice>>() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                remoteInvoiceList = invoices;
                mListener.onInitialized();
            }
            @Override
            public void onFailure(int status) {

            }
        });
    }
    public static void initializeLocalInvoiceList() {
        // Retrieve recotds from local DB
        InvoiceDB invoiceHelper = InvoiceDB.getInstance();
        List<Invoice> invoiceList = invoiceHelper.getAll();
        localInvoiceList = invoiceList;
    }
    public static List<Invoice> getLocalInvoiceList() {
        return localInvoiceList;
    }
    public static Invoice getLocalInvoice(int invoiceIdx) {
        if(localInvoiceList == null)
            return null;
        return localInvoiceList.get(invoiceIdx);
    }
    public static List<Invoice> getRemoteInvoiceList() {
        return remoteInvoiceList;
    }
    public static Invoice getRemoteInvoice(int invoiceIdx) {
        if(remoteInvoiceList == null)
            return null;
        return remoteInvoiceList.get(invoiceIdx);
    }

    // User getter/setter for RestClient
    public static User getUser() {
        return user;
    }
    public static void setUser(User user) {
        Commons.user = user;
    }

    // Getters for ViewPager
    public static String[] getSalesHistoryTabs(Context context) {
        // initialize tab from string xml for localization
        if(SALESHISTORY_CATEGORY == null) {
            SALESHISTORY_CATEGORY = new String[4];
            SALESHISTORY_CATEGORY[0] = context.getString(R.string.tab_local_invoice);
            SALESHISTORY_CATEGORY[1] = context.getString(R.string.tab_remote_invoice);
            SALESHISTORY_CATEGORY[2] = context.getString(R.string.tab_statistics);
            SALESHISTORY_CATEGORY[3] = context.getString(R.string.tab_revenue);
        }
        return SALESHISTORY_CATEGORY;
    }
    public static String[] getCategoryTabs() {
        if(categoryList == null)
            return null;
        return INVENTORY_CATEGORY;
    }

    // Interface used for initializeInventoryList method
    public interface onInitializedListener {
        public void onInitialized();
    }
}
