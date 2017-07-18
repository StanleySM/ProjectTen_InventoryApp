package com.example.android.projectten_inventoryapp.Data;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class ItemDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String NAME_OF_DB = "last_app_DB.db";
        public ItemDBHelper(Context context) {
        super(context, NAME_OF_DB, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ItemInsertion.TABLE_NAME + " ("
                + ItemInsertion._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemInsertion.ITEM_NAME + " TEXT NOT NULL, "
                + ItemInsertion.ITEM_PIECES + " INTEGER NOT NULL DEFAULT 0, "
                + ItemInsertion.ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ItemInsertion.ITEM_IMAGE + " TEXT);";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}