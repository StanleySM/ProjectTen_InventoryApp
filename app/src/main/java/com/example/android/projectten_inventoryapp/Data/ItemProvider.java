package com.example.android.projectten_inventoryapp.Data;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class ItemProvider extends ContentProvider {

    private static final int ITEMS = 20;
    private static final int ITEM_ID = 21;
    private ItemDBHelper xDBHelper;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(ItemContractor.ENTER_NAME, ItemContractor.ITEMS_FOLDERS, ITEMS);
        mUriMatcher.addURI(ItemContractor.ENTER_NAME, ItemContractor.ITEMS_FOLDERS + "/#", ITEM_ID);
    }

    @Override
    public Cursor query(Uri UriValue, String[] tables, String choosen, String[] choosenValues, String itemsOrdering) {
        SQLiteDatabase DB = xDBHelper.getReadableDatabase();

        Cursor cursor;
        int match = mUriMatcher.match(UriValue);
        switch(match) {
            case ITEMS:
                cursor = DB.query(ItemInsertion.TABLE_NAME, tables, choosen, choosenValues, null, null, itemsOrdering);
                break;
            case ITEM_ID:
                choosen = ItemInsertion._ID +"=?";
                choosenValues = new String[] {String.valueOf(ContentUris.parseId(UriValue))};
                cursor = DB.query(ItemInsertion.TABLE_NAME, tables, choosen, choosenValues, null, null, itemsOrdering);
                break;
            default:
                throw new IllegalArgumentException("URI is not defined " + UriValue);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), UriValue);

        return cursor;
    }
    @Override
    public boolean onCreate() {
        xDBHelper = new ItemDBHelper(getContext());
        return true;
    }
    @Override
    public Uri insert(Uri UriValue, ContentValues contentValues) {
        final int match = mUriMatcher.match(UriValue);
        switch(match) {
            case ITEMS:
                return writeItemToDB(UriValue, contentValues);
            default:
                throw new IllegalArgumentException("Insertion failed due " + UriValue);
        }
    }
    @Override
    public String getType(Uri UriValue) {
        final int SameValues = mUriMatcher.match(UriValue);
        switch (SameValues) {
            case ITEMS:
                return ItemInsertion.LIST_TYPES;
            case ITEM_ID:
                return ItemInsertion.ITEM_TYPES;
            default:
                throw new IllegalStateException("Wrong URI " + UriValue + " due " + SameValues);
        }
    }

    private Uri writeItemToDB(Uri UriValue, ContentValues ItemValues) {
        String name = ItemValues.getAsString(ItemInsertion.ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Missing name of Item");
        }

        Integer pieces = ItemValues.getAsInteger(ItemInsertion.ITEM_PIECES);
        if (pieces != null && pieces < 0) {
            throw new IllegalArgumentException("Wrong number of pieces");
        }

        Float price = ItemValues.getAsFloat(ItemInsertion.ITEM_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Wront price");
        }

        SQLiteDatabase DB = xDBHelper.getReadableDatabase();
        long id = DB.insert(ItemInsertion.TABLE_NAME, null, ItemValues);

        getContext().getContentResolver().notifyChange(UriValue, null);

        return ContentUris.withAppendedId(UriValue, id);
    }


    private int changeItemValues(Uri UriValues, ContentValues values, String choosen, String[] choosenValues) {
        if (values.containsKey(ItemInsertion.ITEM_NAME)) {
            String name = values.getAsString(ItemInsertion.ITEM_NAME);
            if (name == null) {throw new IllegalArgumentException("Missing name of Item");}
        }

        if (values.containsKey(ItemInsertion.ITEM_PIECES)) {
            Integer pieces = values.getAsInteger(ItemInsertion.ITEM_PIECES);
            if (pieces != null && pieces < 0) {
                throw new IllegalArgumentException("Wrong number of pieces");
            }
        }

        if (values.containsKey(ItemInsertion.ITEM_PRICE)) {
            Float price = values.getAsFloat(ItemInsertion.ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Wront price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase DB = xDBHelper.getReadableDatabase();
        int updateTableRow = DB.update(ItemInsertion.TABLE_NAME, values, choosen, choosenValues);
        if (updateTableRow != 0) {
            getContext().getContentResolver().notifyChange(UriValues, null);
        }
        return updateTableRow;
    }

    @Override
    public int delete(Uri UriValue, String choosen, String[] choosenValues) {
        SQLiteDatabase DB = xDBHelper.getWritableDatabase();

        int deleteTableRow;

        final int match = mUriMatcher.match(UriValue);
        switch (match) {
            case ITEMS:
                deleteTableRow = DB.delete(ItemInsertion.TABLE_NAME, choosen, choosenValues);
                break;
            case ITEM_ID:
                choosen = ItemInsertion._ID + "=?";
                choosenValues = new String[]{String.valueOf(ContentUris.parseId(UriValue))};
                deleteTableRow =  DB.delete(ItemInsertion.TABLE_NAME, choosen, choosenValues);
                break;
            default:
                throw new IllegalArgumentException("Delete of item failed due " + UriValue);
        }

        if (deleteTableRow != 0) {
            getContext().getContentResolver().notifyChange(UriValue, null);
        }
        return deleteTableRow;
    }

    @Override
    public int update(Uri UriValue, ContentValues contentValues, String choosen, String[] choosenValues) {
        final int match = mUriMatcher.match(UriValue);
        switch (match) {
            case ITEMS:
                return changeItemValues(UriValue, contentValues, choosen, choosenValues);
            case ITEM_ID:
                choosen = ItemInsertion._ID + "=?";
                choosenValues = new String[] { String.valueOf(ContentUris.parseId(UriValue)) };
                return changeItemValues(UriValue, contentValues, choosen, choosenValues);
            default:
                throw new IllegalArgumentException("Update of item failed due " + UriValue);
        }
    }

}