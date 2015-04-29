package com.hkust.comp4521.hippos.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TC on 4/29/2015.
 */
public class Commons {
    private static String[] INVENTORY_CATEGORY = {"Unsorted", "Books", "Confectionery", "Toys", "Stationery"};
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

    public static String[] getCategories() {
        return INVENTORY_CATEGORY;
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
        list.add(new Inventory("Dog Doll", 0, 100));
        list.add(new Inventory("The Flight Book", 0, 100));
        list.add(new Inventory("Poster Card", 0, 100));
        list.add(new Inventory("Butterfly Bookmark", 0, 100));

        // Books
        list = inventoryList.get(1);
        list.add(new Inventory("Harry Potter", 0, 100));
        list.add(new Inventory("Tuesday wth Morrie", 0, 100));
        list.add(new Inventory("Alice's Adventures in Wonderland", 0, 100));
        list.add(new Inventory("Moby-Dick", 0, 100));

        // Confectionery
        list = inventoryList.get(2);
        list.add(new Inventory("Skittles", 0, 100));
        list.add(new Inventory("M&Ms", 0, 100));
        list.add(new Inventory("Milk Candy", 0, 100));

        // Toys
        list = inventoryList.get(3);
        list.add(new Inventory("Lego Box Set", 0, 100));
        list.add(new Inventory("RC Car", 0, 100));
        list.add(new Inventory("Toy Doll", 0, 100));
        list.add(new Inventory("Rubber Duck", 0, 100));

        // Stationery
        list = inventoryList.get(4);
        list.add(new Inventory("Pencil", 0, 100));
        list.add(new Inventory("Stainless Steel Pen", 0, 100));
        list.add(new Inventory("Correction Tape", 0, 100));
        list.add(new Inventory("A4 Notebook", 0, 100));
    }
}
