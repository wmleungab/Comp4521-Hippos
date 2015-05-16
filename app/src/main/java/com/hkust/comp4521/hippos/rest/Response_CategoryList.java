package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Category;

import java.util.List;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_CategoryList {
    public boolean error;
    public String message;
    List<Category> category;

    public List<Category> getCategoryList() {
        return this.category;
    }
}
