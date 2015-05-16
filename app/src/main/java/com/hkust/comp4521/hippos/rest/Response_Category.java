package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Category;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_Category extends Category {
    public boolean error;
    public String message;

    public Response_Category(int id, String name) {
        super(id, name);
    }

    public Category getCategory() {
        return (Category) this;
    }
}
