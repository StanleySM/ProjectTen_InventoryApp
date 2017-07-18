package com.example.android.projectten_inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_BUFFER = 0;
    private ItemsCursorAdapter xCursorAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] viewValues = {ItemInsertion._ID, ItemInsertion.ITEM_NAME, ItemInsertion.ITEM_PIECES, ItemInsertion.ITEM_PRICE, ItemInsertion.ITEM_IMAGE};

        return new CursorLoader(this, ItemInsertion.URI_PATH, viewValues, null, null, null);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        FloatingActionButton CatPaw = (FloatingActionButton) findViewById(R.id.cat_paw);
        CatPaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAddNewItemWindow();
            }
        });

        ListView itemList = (ListView) findViewById(R.id.list_view_product);

        View listView = findViewById(R.id.list_view);
        itemList.setEmptyView(listView);

        xCursorAdapter = new ItemsCursorAdapter(this, null);
        itemList.setAdapter(xCursorAdapter);

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChangeItemValues.class);
                Uri actualURI = ContentUris.withAppendedId(ItemInsertion.URI_PATH, id);
                intent.setData(actualURI);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ITEM_BUFFER, null, this);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor savedValues) {
        xCursorAdapter.swapCursor(savedValues);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        xCursorAdapter.swapCursor(null);
    }

    private void viewAddNewItemWindow() {
        AddNewItem newItem = new AddNewItem();
        newItem.show(getSupportFragmentManager(), getString(R.string.add_item_button));
    }


}