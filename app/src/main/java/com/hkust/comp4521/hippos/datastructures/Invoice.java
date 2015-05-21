package com.hkust.comp4521.hippos.datastructures;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Yman on 15/5/2015.
 */
public class Invoice {

    // Attributes
    int id;
    double total_price;
    double final_price;
    double paid;
    String date_time;
    String user;
    String content;
    String email;
    int status;
    List<InvoiceInventory> invoiceInventories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public void setTotalPrice(double total_price) {
        this.total_price = total_price;
    }

    public double getFinalPrice() {
        return final_price;
    }

    public void setFinalPrice(double final_price) {
        this.final_price = final_price;
    }

    public double getPaid() {
        return this.paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
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

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<InvoiceInventory> getInvoiceInventories() {
        deserializeInvoiceInventories();
        return invoiceInventories;
    }

    public void setInvoiceInventories(List<InvoiceInventory> invoiceInventories) {
        this.invoiceInventories = invoiceInventories;
        serializeInvoiceInventories();
    }

    private void serializeInvoiceInventories() {
        JSONObject jsonObj = new JSONObject();
        // put each InvoiceInventory into json object
        // with format of "Inventory ID : Quantity"
        try {
            for (InvoiceInventory inv : this.invoiceInventories) {
                jsonObj.put(inv.getInventory().getId() + "", inv.getQuantity() + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        content = jsonObj.toString();
    }

    private void deserializeInvoiceInventories() {
        JSONObject jsonObj = null;
        invoiceInventories = new ArrayList<InvoiceInventory>();
        try {
            jsonObj = new JSONObject(content);
            // Iterate each key-value pair and generate InvoiceInventory items accordingly
            Iterator<?> keys = jsonObj.keys();
            while(keys.hasNext()) {
                String key = (String)keys.next();
                int value = jsonObj.getInt(key);

                int inventoryId = Integer.parseInt(key);

                invoiceInventories.add(new InvoiceInventory(Commons.getInventory(inventoryId), value));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
