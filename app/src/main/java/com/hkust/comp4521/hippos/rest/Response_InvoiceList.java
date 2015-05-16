package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Invoice;

import java.util.List;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_InvoiceList {
    public boolean error;
    public String message;
    List<Invoice> invoice;

    public List<Invoice> getInvoiceList() {
        return invoice;
    }
}
