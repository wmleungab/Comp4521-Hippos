package com.hkust.comp4521.hippos.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hkust.comp4521.hippos.datastructures.Invoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Yman on 21/5/2015.
 */
public class InvoiceDB {

    public static final String TABLE_NAME = "invoice";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_FINAL_PRICE = "final_price";
    public static final String COLUMN_PAID = "paid";
    public static final String COLUMN_DATETIME = "date_time";
    // public static final String COLUMN_USER = "user";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_STATUS = "status";
    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TOTAL_PRICE + " real not null, "
            + COLUMN_FINAL_PRICE + " real not null, "
            + COLUMN_PAID + " real not null, "
            + COLUMN_DATETIME + " text not null, "
            // + COLUMN_USER + " text not null, "
            + COLUMN_CONTENT + " text not null, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_STATUS + " integer not null "
            + ");";
    public static InvoiceDB instance;
    private SQLiteDatabase db;

    private InvoiceDB() {
        db = DatabaseHelper.getDatabase();
    }

    public static InvoiceDB getInstance() {
        if (instance == null) {
            instance = new InvoiceDB();
        }
        return instance;
    }

    public void close() {
        db.close();
    }

    public Invoice insert(Invoice invoice) {
        //set the time stamp
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        invoice.setDateTime(sdf.format(cal.getTime()));
        //

        //validate email, if invalid return null
        String email = invoice.getEmail();
        int adLocation = email.indexOf("@");
        if (adLocation <= 0 || adLocation == email.length() - 1)
            return null;

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TOTAL_PRICE, invoice.getTotalPrice());
        cv.put(COLUMN_FINAL_PRICE, invoice.getFinalPrice());
        cv.put(COLUMN_PAID, invoice.getPaid());
        cv.put(COLUMN_DATETIME, invoice.getDateTime());
        cv.put(COLUMN_CONTENT, invoice.getContent());
        cv.put(COLUMN_EMAIL, invoice.getEmail());
        cv.put(COLUMN_STATUS, invoice.getStatus());

        long id = db.insert(TABLE_NAME, null, cv);

        invoice.setId((int) id);
        return invoice;
    }

    public boolean update(Invoice invoice) {
        //set the time stamp
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        invoice.setDateTime(sdf.format(cal.getTime()));
        //

        //validate email, if invalid return null
        String email = invoice.getEmail();
        int adLocation = email.indexOf("@");
        if (adLocation <= 0 || adLocation == email.length() - 1)
            return false;

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TOTAL_PRICE, invoice.getTotalPrice());
        cv.put(COLUMN_FINAL_PRICE, invoice.getFinalPrice());
        cv.put(COLUMN_PAID, invoice.getPaid());
        cv.put(COLUMN_DATETIME, invoice.getDateTime());
        cv.put(COLUMN_CONTENT, invoice.getContent());
        cv.put(COLUMN_EMAIL, invoice.getEmail());
        cv.put(COLUMN_STATUS, invoice.getStatus());

        String where = COLUMN_ID + "=" + invoice.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }


    public boolean delete(final long id) {
        String where = COLUMN_ID + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public List<Invoice> getAll() {
        List<Invoice> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public Invoice get(long id) {
        Invoice item = null;
        String where = COLUMN_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            item = getRecord(result);
        }

        result.close();
        return item;
    }

    private Invoice getRecord(Cursor cursor) {
        Invoice invoice = new Invoice();
        invoice.setId(cursor.getInt(0));
        invoice.setTotalPrice(cursor.getDouble(1));
        invoice.setFinalPrice(cursor.getDouble(2));
        invoice.setPaid(cursor.getDouble(3));
        invoice.setDateTime(cursor.getString(4));
        invoice.setContent(cursor.getString(5));
        invoice.setEmail(cursor.getString(6));
        invoice.setStatus(cursor.getInt(7));

        return invoice;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
}
