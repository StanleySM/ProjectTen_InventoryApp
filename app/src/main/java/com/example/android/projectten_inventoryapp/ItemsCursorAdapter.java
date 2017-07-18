package com.example.android.projectten_inventoryapp;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class ItemsCursorAdapter extends CursorAdapter {

    private Context xContext;

    public ItemsCursorAdapter(Context context, Cursor cursor) {super(context, cursor, 0);}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        xContext = context;

        TextView itemName = (TextView) view.findViewById(R.id.name);
        ImageView itemImage = (ImageView) view.findViewById(R.id.image);
        TextView itemPieces = (TextView) view.findViewById(R.id.pieces_text);
        TextView itemPrice = (TextView) view.findViewById(R.id.price_text);

        final String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemInsertion.ITEM_NAME));
        final String image = cursor.getString(cursor.getColumnIndexOrThrow(ItemInsertion.ITEM_IMAGE));
        final Integer pieces = cursor.getInt(cursor.getColumnIndexOrThrow(ItemInsertion.ITEM_PIECES));
        final Integer price = cursor.getInt(cursor.getColumnIndexOrThrow(ItemInsertion.ITEM_PRICE));


        itemName.setText(name);
        itemPieces.setText(Integer.toString(pieces));
        itemPrice.setText(Integer.toString(price));

        if(image == null) {

            itemImage.setVisibility(View.GONE);

        }

        else {

            itemImage.setVisibility(View.VISIBLE);
            itemImage.setImageURI(Uri.parse(image));

        }

        Button listSellButton = (Button) view.findViewById(R.id.sell_by_one_button);
        listSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {

                    Object ItemView = view.getTag();
                    String stringValue = ItemView.toString();
                    ContentValues itemVALUES = new ContentValues();
                    itemVALUES.put(ItemInsertion.ITEM_NAME, name);
                    itemVALUES.put(ItemInsertion.ITEM_IMAGE, image);
                    itemVALUES.put(ItemInsertion.ITEM_PIECES, pieces >= 1? pieces-1: 0);
                    itemVALUES.put(ItemInsertion.ITEM_PRICE, price);

                    Uri currentPetUri = ContentUris.withAppendedId(ItemInsertion.URI_PATH, Integer.parseInt(stringValue));

                    int rowsAffected = xContext.getContentResolver().update(currentPetUri, itemVALUES, null, null);
                    if (rowsAffected == 0 || pieces == 0) {
                        Toast.makeText(xContext, xContext.getString(R.string.error_toSell_item), Toast.LENGTH_LONG).show();

                    }
                }
            }});


        Button listBuyButton = (Button) view.findViewById(R.id.buy_by_one_button);
        listBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {

                    Object ItemView = view.getTag();
                    String stringValue = ItemView.toString();
                    ContentValues itemVALUES = new ContentValues();
                    itemVALUES.put(ItemInsertion.ITEM_NAME, name);
                    itemVALUES.put(ItemInsertion.ITEM_IMAGE, image);
                    itemVALUES.put(ItemInsertion.ITEM_PIECES, pieces >= 1? pieces+1: 999);
                    itemVALUES.put(ItemInsertion.ITEM_PRICE, price);

                    Uri currentPetUri = ContentUris.withAppendedId(ItemInsertion.URI_PATH, Integer.parseInt(stringValue));
                    xContext.getContentResolver().update(currentPetUri, itemVALUES, null, null);

                }
            }});

        Object itemValue = cursor.getInt(cursor.getColumnIndex(ItemInsertion._ID));
        listSellButton.setTag(itemValue);
        listBuyButton.setTag(itemValue);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

}