package com.hkust.comp4521.hippos.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hkust.comp4521.hippos.datastructures.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yman on 21/5/2015.
 */
public class CategoryDB {
    public static final String TABLE_NAME = "category";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key, "
            + COLUMN_NAME + " text not null " + ");";
    public static CategoryDB instance;
    private SQLiteDatabase db;

    private CategoryDB() {
        db = DatabaseHelper.getDatabase();
    }

    public static CategoryDB getInstance() {
        if (instance == null) {
            instance = new CategoryDB();
        }
        return instance;
    }

    public void close() {
        db.close();
    }

    public Category insert(Category c) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, c.getID());
        cv.put(COLUMN_NAME, c.getName());

        long id = db.insert(TABLE_NAME, null, cv);
        c.setID((int) id);
        return c;
    }

    public boolean update(Category c) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, c.getID());
        cv.put(COLUMN_NAME, c.getName());

        String where = COLUMN_ID + " = " + c.getID();
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    //There should not be a delete operation anyway
    private boolean delete(int cate_id) {
        String where = COLUMN_ID + " = " + cate_id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<Category>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            categories.add(getRecord(cursor));
        }

        cursor.close();
        return categories;
    }

    public Category get(int id) {
        Category c = null;
        String where = COLUMN_ID + " = " + id;
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null);

        if (cursor.moveToFirst()) {
            c = getRecord(cursor);
        }

        cursor.close();
        return c;
    }

    public Category getRecord(Cursor cursor) {
        Category result = new Category();

        result.setID(cursor.getInt(0));
        result.setName(cursor.getString(1));

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
