package com.hkust.comp4521.hippos.rest;

import com.hkust.comp4521.hippos.datastructures.Invoice;

/**
 * Created by Yman on 16/5/2015.
 */
public class Response_Invoice extends Invoice {
    public boolean error;
    public String message;

    public Invoice getInvoice() {
        return (Invoice) this;
    }
}
