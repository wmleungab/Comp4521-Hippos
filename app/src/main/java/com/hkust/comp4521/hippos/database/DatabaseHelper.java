package com.hkust.comp4521.hippos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Commons;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase database;

    private DatabaseHelper(Context context) {
        super(context, Commons.APP_ROOT_PATH + DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase initDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new DatabaseHelper(context).getWritableDatabase();
        }
        return database;
    }

    public static SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            return null;
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(InventoryDB.CREATE_TABLE);
        database.execSQL(InvoiceDB.CREATE_TABLE);
        database.execSQL(CategoryDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + InventoryDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InvoiceDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDB.TABLE_NAME);
        onCreate(db);
    }

}
