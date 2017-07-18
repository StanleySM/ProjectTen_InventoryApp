package com.example.android.projectten_inventoryapp.Data;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContractor {
    public static final String ENTER_NAME = "com.example.android.projectten_inventoryapp";
    private static final Uri URI_ITEMS_TABLE = Uri.parse("content://" + ENTER_NAME);
    public static final String ITEMS_FOLDERS = "items";

    public static final class ItemInsertion implements BaseColumns {

        public static final String LIST_TYPES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ENTER_NAME + "/" + ITEMS_FOLDERS;
        public static final String ITEM_TYPES = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ENTER_NAME + "/" + ITEMS_FOLDERS;

        public static final Uri URI_PATH = Uri.withAppendedPath(URI_ITEMS_TABLE, ITEMS_FOLDERS);

        public final static String TABLE_NAME = "items";
        public final static String _ID = BaseColumns._ID;
        public final static String ITEM_NAME ="name";
        public final static String ITEM_PIECES = "pieces";
        public final static String ITEM_PRICE = "price";
        public final static String ITEM_IMAGE = "image";
    }

}