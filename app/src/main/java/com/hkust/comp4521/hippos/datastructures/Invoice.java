package com.hkust.comp4521.hippos.datastructures;

import java.util.List;

/**
 * Created by Yman on 15/5/2015.
 */
public class Invoice {

    // Attributes
    int id;
    double total_price;
    double final_price;
    String date_time;
    String user;
    String content;
    String email;
    int status;

    List<InvoiceInventory> invoiceInventories;

    public int getID() {
        return id;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public double getFinalPrice() {
        return final_price;
    }

    public String getDateTime() {
        return date_time;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public List<InvoiceInventory> getInvoiceInventories() {
        return invoiceInventories;
    }

    public void setInvoiceInventories(List<InvoiceInventory> invoiceInventories) {
        this.invoiceInventories = invoiceInventories;
    }
}
