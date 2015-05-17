package com.hkust.comp4521.hippos.datastructures;

/**
 * Created by Yman on 15/5/2015.
 */
public class Category {
    private int id;
    private String name;

    public Category(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
