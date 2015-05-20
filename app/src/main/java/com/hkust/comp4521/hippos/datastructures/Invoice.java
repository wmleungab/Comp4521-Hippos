package com.hkust.comp4521.hippos.datastructures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void setDateTime(String s) {
        this.date_time = s;
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

    public Date getDate() {
        SimpleDateFormat sdf;
        Date date = null;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = sdf.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ;
        return date;
    }
}
