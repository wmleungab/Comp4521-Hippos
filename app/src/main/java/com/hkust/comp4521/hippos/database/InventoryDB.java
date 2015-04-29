package com.hkust.comp4521.hippos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hkust.comp4521.hippos.datastructures.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TC on 4/2/2015.
 */
public class InventoryDB {

    public static final String TABLE_NAME = "inventory";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";

    private static final String CREATE_TABLE = "create table "
                                                + TABLE_NAME + "("
                                                + COLUMN_ID + " integer primary key autoincrement, "
                                                + COLUMN_NAME + " text not null,"
                                                + COLUMN_CATEGORY + " integer not null,"
                                                + COLUMN_PRICE + " real not null"
                                                + ");";

    private SQLiteDatabase db;

    public InventoryDB(Context context) {
        db = DatabaseHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public Inventory insert(Inventory item) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_CATEGORY, item.getCatId());
        cv.put(COLUMN_PRICE, item.getPrice());

        long id = db.insert(TABLE_NAME, null, cv);

        item.setId(id);
        return item;
    }

    public boolean update(Inventory item) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_CATEGORY, item.getCatId());
        cv.put(COLUMN_PRICE, item.getPrice());

        String where = COLUMN_ID + "=" + item.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id){
        String where = COLUMN_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    public List<Inventory> getAll() {
        List<Inventory> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public Inventory get(long id) {
        Inventory item = null;
        String where = COLUMN_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            item = getRecord(result);
        }

        result.close();
        return item;
    }

    public Inventory getRecord(Cursor cursor) {
        Inventory result = new Inventory();

        result.setId(cursor.getLong(0));
        result.setName(cursor.getString(1));
        result.setCatId(cursor.getInt(2));
        result.setPrice(cursor.getFloat(3));

        return result;
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
