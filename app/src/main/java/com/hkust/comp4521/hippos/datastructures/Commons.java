package com.hkust.comp4521.hippos.datastructures;

import java.util.ArrayList;
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

    private static ArrayList<ArrayList<Inventory>> inventoryList = null;
    private static int lastUpdate = -1;

    public static int getCategoryCount() {
        if(inventoryList == null) {
            initializeInventoryList();
        }
        return inventoryList.size();
    }

    public static ArrayList<Inventory> getInventoryList(int index) {
        if(inventoryList == null) {
            initializeInventoryList();
        }
        return inventoryList.get(index);
    }

    public static Inventory getInventory(int cIndex, int iIndex) {
        if(inventoryList == null) {
            initializeInventoryList();
        }
        return inventoryList.get(cIndex).get(iIndex);
    }

    public static String[] getCategoryTabs() {
        return INVENTORY_CATEGORY;
    }

    public static Inventory getRandomInventory() {
        int catId = (int) Math.random() * getCategoryCount();
        ArrayList<Inventory> list = inventoryList.get(catId);
        int invId = (int) Math.random() * list.size();
        Inventory toReturn = list.get(invId);
        return toReturn;
    }

    public static String[] getSalesHistoryTabs() {
        return SALESHISTORY_CATEGORY;
    }

    private static void initializeInventoryList() {
        // TODO: fetch list from server instead
        inventoryList = new  ArrayList<ArrayList<Inventory>>();
        inventoryList.add(new ArrayList<Inventory>());
        inventoryList.add(new ArrayList<Inventory>());
        inventoryList.add(new ArrayList<Inventory>());
        inventoryList.add(new ArrayList<Inventory>());
        inventoryList.add(new ArrayList<Inventory>());
        ArrayList<Inventory> list = null;

        // Unsorted
        list = inventoryList.get(0);
        list.add(new Inventory(1,"Dog Doll", 0, 100, "dog_doll"));
        list.add(new Inventory(2,"The Flight Book", 0, 100, "flight_book"));
        list.add(new Inventory(3,"Poster Card", 0, 100, "poster_card"));
        list.add(new Inventory(4,"Butterfly Bookmark", 0, 100, "butterfly_bookmark"));

        // Books
        list = inventoryList.get(1);
        list.add(new Inventory(5,"Harry Potter", 1, 100, "harry_potter"));
        list.add(new Inventory(6,"Tuesdays wth Morrie", 1, 100, "tuesdays_with_morrie"));
        list.add(new Inventory(7,"Alice's Adventures in Wonderland", 1, 100, "alice_adv_in_wonderland"));
        list.add(new Inventory(8,"Moby Dick", 1, 100, "moby_dick"));

        // Confectionery
        list = inventoryList.get(2);
        list.add(new Inventory(9,"Skittles", 2, 100, "skittles"));
        list.add(new Inventory(10,"M&Ms", 2, 100, "m_and_m"));
        list.add(new Inventory(11,"Milk Ball", 2, 100, "milk_ball"));

        // Toys
        list = inventoryList.get(3);
        list.add(new Inventory(12,"Lego Box Set", 3, 100, "lego_boxset"));
        list.add(new Inventory(13,"RC Car", 3, 100, "rc_car"));
        list.add(new Inventory(14,"Toy Doll", 3, 100, "toy_doll"));
        list.add(new Inventory(15,"Rubber Duck", 3, 100, "rubber_duck"));

        // Stationery
        list = inventoryList.get(4);
        list.add(new Inventory(16,"Pencil", 4, 100, "pencil"));
        list.add(new Inventory(17,"Stainless Steel Pen", 4, 100, "steel_pen"));
        list.add(new Inventory(18,"Correction Tape", 4, 100, "correction_tape"));
        list.add(new Inventory(19,"A4 Notebook", 4, 100, "a4_notebook"));
    }
}
